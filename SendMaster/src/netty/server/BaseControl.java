package netty.server;

import com.circle.core.util.Verification;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;

/**
 * @author Created by clicoy on 15-5-10.
 */
public abstract class BaseControl {
    private final Logger logger = LoggerFactory.getLogger(BaseControl.class);
    public static final String USER_AGENT = "User-Agent";
    public static final String IOS = "ios";
    public static final String ANDROID = "android";
    public HttpPostRequestDecoder request;
    public HttpRequest req;
    public ChannelHandlerContext ctx;
    public String token;

    public FullHttpResponse run(String uri, ChannelHandlerContext ctx,
                                HttpPostRequestDecoder request, Class controllerClass) throws Exception {
        this.request = request;
        this.ctx = ctx;
        String url = uri.split(SplitStr.URL_QUE)[0];
        Urls.URL url_obj = Urls.getUrls().get(url);
        String methodName = url_obj.methodName;
//        logger.warn(uri + " methodName=" + methodName + " mobile=" + request.getBodyHttpData("mob"));
        Method method = url_obj.method;
        //获取参数
        if (method != null) {//判断获得的 方法是否未空
            Object object;
            token = strings(JsonParams.TOK);
            object = method.invoke(this);
            return (FullHttpResponse) object;
        } else {
            return HttpBack.back_404(ctx);
        }
    }

    public void setReq(HttpRequest req) {
        this.req = req;
    }


    public String strings(String key) {
        if (key == null) return null;
        return HttpBack.params(request, key);
    }

    public int integers(String key, int def) {
        String value = HttpBack.params(request, key);
        return Verification.getInt(def, value);
    }

    public double doubles(String key, double def) {
        String value = HttpBack.params(request, key);
        return Verification.getDoule(def, value);
    }

    public float floats(String key, float def) {
        String value = HttpBack.params(request, key);
        return Verification.getFloat(def, value);
    }

    public String agentSystem() {
        CharSequence agent = req.headers().get(USER_AGENT);
        logger.debug(USER_AGENT + " : " + agent.toString());
        if (agent.toString().toLowerCase().contains(IOS.toLowerCase())) {
            return IOS;
        } else if (agent.toString().toLowerCase().contains(ANDROID.toLowerCase())) {
            return ANDROID;
        }
        return null;
    }

    /**
     * 获取手机系统版本
     */
    public String phoneSystem() {
        CharSequence agent = req.headers().get(USER_AGENT);
        logger.debug(USER_AGENT + " : " + agent.toString());
        if (agent.toString().toLowerCase().contains(IOS.toLowerCase())) {
            return IOS;
        } else if (agent.toString().toLowerCase().contains(ANDROID.toLowerCase())) {
            return ANDROID;
        }
        return null;
    }

    /**
     * 获取软件版本
     */
    public String appVersion() {
        CharSequence agent = req.headers().get(USER_AGENT);
        logger.debug(USER_AGENT + " : " + agent.toString());
        if (agent.toString().toLowerCase().contains(IOS.toLowerCase())) {
            return IOS;
        } else if (agent.toString().toLowerCase().contains(ANDROID.toLowerCase())) {
            return ANDROID;
        }
        return null;
    }

    public SocketAddress remoteAddress() {
        return ctx.channel().remoteAddress();
    }


    public String hostIp() {
        String hosts = remoteAddress().toString();
        if (hosts != null) {
            String[] hps = hosts.substring(1).split(":");
            if (hps.length == 2)
                return hps[0];
        }
        return null;
    }

    public String runAsyn(String uri, ChannelHandlerContext ctx, HttpPostRequestDecoder request, Class controllerClass) throws InvocationTargetException, IllegalAccessException {
        this.request = request;
        this.ctx = ctx;
        String url = uri.split(SplitStr.URL_QUE)[0];
        Urls.URL url_obj = Urls.getUrls().get(url);
        String methodName = url_obj.methodName;
//        logger.warn(uri + " methodName=" + methodName + " mobile=" + request.getBodyHttpData("mob"));
        Method method = url_obj.method;
        //获取参数
        if (method != null) {//判断获得的 方法是否未空
            Object object;
            token = strings(JsonParams.TOK);
            object = method.invoke(this);
            return object.toString();
        } else {
            HttpBack.asyn_back_404(ctx);
            return ErrorCode.ERR_404;
        }
    }
}
