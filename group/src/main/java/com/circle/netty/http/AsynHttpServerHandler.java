package com.circle.netty.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class AsynHttpServerHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private Logger logging = LoggerFactory.getLogger(AsynHttpServerHandler.class);
    private static final String SPACE = " ";
    private ApplicationContext context;
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
    public AsynHttpServerHandler(ApplicationContext context) {
        this.context = context;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpRequest httpRequest) throws Exception {
        String uri = httpRequest.uri();
        String url = uri.split(SplitStr.URL_QUE)[0];
        HttpPostRequestDecoder request = new HttpPostRequestDecoder(factory, httpRequest);
        List data = request.getBodyHttpDatas(); //缓存参数-异步处理
        ctx.writeAndFlush(HttpBack.back_200(ctx));
        ctx.close();
        //异步处理
        logging.info(uri + SPACE + data);
        BaseControl base = null;
        long start = System.currentTimeMillis();
        try {
            Urls.URL urls = Urls.getUrls().get(url);
            Class controllerClass = null;
            if (urls != null) {
                Object object = context.getBean(urls.parent);
                controllerClass = object.getClass();
                base = (BaseControl) object;
                base.setReq(httpRequest);
            }
            String back = null;
            if (base != null)
                back = base.runAsyn(uri, ctx, request, controllerClass);
            if (back == null || !ErrorCode.OK.equals(back)) {
                logging.warn(uri + SPACE + request.getBodyHttpDatas() + SPACE + back);
            }
        } catch (Exception e) {
            logging.error(uri + SPACE + data + " ,ErrorCode=" + ErrorCode.SYS_ERR, e);
        }
        logging.debug(uri + " west time " + (System.currentTimeMillis() - start) + " ms");
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        ctx.close();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logging.error("NETTY-ERROR. " + cause.getMessage(), cause);
        ctx.close();
    }
}
