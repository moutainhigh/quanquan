package com.circle.netty.http;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private Logger logging = LoggerFactory.getLogger(HttpServerHandler.class);
    private static final String SPACE = " ";
    private ApplicationContext context;

    public HttpServerHandler(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logging.error("NETTY-ERROR. " + cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
        // get request uri
        logging.debug("@@@@@@@@@@@@@@@@@@@@ url="+ msg.uri());
        String uri = msg.uri();
        String url = uri.split(SplitStr.URL_QUE)[0];
        if (url.equals("/")) {
            HttpBack.indexHtml(ctx);
            return;
        }
        HttpPostRequestDecoder request = new HttpPostRequestDecoder(msg);
        logging.info("from=" + ctx.channel().remoteAddress().toString() + " url=" + uri + SPACE + request.getBodyHttpDatas());
        long start = System.currentTimeMillis();
        BaseControl base = null;
        FullHttpResponse response;
        try {
            Urls.URL urls = Urls.getUrls().get(url);
            Class controllerClass = null;
            if (urls != null) {
                Object object = context.getBean(urls.parent);
                controllerClass = object.getClass();
                base = (BaseControl) object;
            }
            if (base != null) {
                base.setReq(msg);
                response = base.run(uri, ctx, request, controllerClass);
            } else {
                response = HttpBack.back_404();
            }
        } catch (Exception e) {
            response = HttpBack.back_500();
            logging.error(uri + request.getBodyHttpDatas() + ErrorCode.SYS_ERR, e);
        }
//        if (HttpHeaderUtil.is100ContinueExpected(msg)) {
//            ctx.writeAndFlush(response);
//        }
//        boolean keepAlive = HttpHeaderUtil.isKeepAlive(msg);
//        if (!keepAlive) {
//            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
//        } else {
//            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
//            ctx.writeAndFlush(response);
//        }
        ChannelFuture f = ctx.writeAndFlush(response);
        if (!HttpHeaderUtil.isKeepAlive(msg) || response.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
        logging.debug(uri + " west time = " + (System.currentTimeMillis() - start) + "ms");
//        ctx.close();
    }
}
