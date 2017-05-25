package com.circle.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.Executors;

/**
 * An HTTP server that sends back the content of the received HTTP request
 * in a pretty plaintext form.
 */
@Component(value = "server")
public class HttpServer {
    private boolean isdebuglog;
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;
    public static String pid;
    public static HttpServer server;
    public static String pidpath = "./";

    public void setIsdebuglog(boolean isdebuglog) {
        this.isdebuglog = isdebuglog;
    }

    public void runHttp(int port, boolean asyn, ApplicationContext context, String pidpath) throws Exception {
        // Configure the server.
        server = this;
        bossGroup = new NioEventLoopGroup(0x1, Executors.newCachedThreadPool()); //mainReactor    1个线程
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 0x3, Executors.newCachedThreadPool());
        HttpServer.pidpath = pidpath;
        pid = String.valueOf(getPid());
        pidfile(pidpath);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            if (isdebuglog) {
                LoggingHandler loghandler = new LoggingHandler(LogLevel.INFO);
                bootstrap.handler(loghandler);
            }
            bootstrap.group(bossGroup, workerGroup)
                    .option(ChannelOption.SO_BACKLOG, 1048576)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_REUSEADDR, true)     //重用地址
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)// heap buf 's better
                    .childOption(ChannelOption.SO_RCVBUF, 1048576)
                    .childOption(ChannelOption.SO_SNDBUF, 1048576);
            if (!asyn) {
                HttpServerInitializer httpServerInitializer = new HttpServerInitializer(null, context);
                bootstrap.childHandler(httpServerInitializer);
            } else {
                AsynHttpServerInitializer httpServerInitializer = new AsynHttpServerInitializer(null, context);
                bootstrap.childHandler(httpServerInitializer);
            }
            Channel ch = bootstrap.bind(port).sync().channel();
            System.out.println("Open your web browser and navigate to " + "http" + "://127.0.0.1:" + port + '/');
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void close() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
//        try {
//            //GROUP.zooKeeper.close();
//        } catch (InterruptedException e) {
//        }
    }

    public static int getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName(); // format: "pid@hostname"
        try {
            int pids = Integer.parseInt(name.substring(0, name.indexOf('@')));
            pid = String.valueOf(pids);
            return pids;
        } catch (Exception e) {
            return -1;
        }
    }

    public static void pidfile(String path) throws IOException {
        if (path != null) {
            pidpath = path;
        }
        String dir = pidpath.substring(0, pidpath.lastIndexOf('/'));
        File dirfile = new File(dir);
        if (!dirfile.isDirectory() || !dirfile.exists()) {
            dirfile.mkdirs();
        }
        File file = new File(pidpath);
        if (!file.exists()) file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        out.write(String.valueOf(getPid()).getBytes());
        out.close();
    }
}
