package netty.server;

import java.io.IOException;
import com.circle.core.util.Config;
/**
 * all types  : Java IOS Andorid WEB Hadoop .NET
 * @author Created by Fomky on 2015/3/5 0005.
 */
public class AppConfig {
    /**
     * 请求内容最大长度 1M
     */
    public static int MAX_CONTENT_LENGTH=1024000;
    public static int SERVER_PORT;
    public static String igexin_appid;
    public static String igexin_appkey;
    public static String igexin_master;
    public static String igexin_host;
//    public static String pid_path;
//    public static ProducerPool<String,String> producerPool;
//    gexin.api.igexin=http://sdk.open.api.igexin.com/apiex.htm
//    gexin.appid=lKqaY0NdFJ6OP25ZysfEE4
//    gexin.appkey=Dz01FOEtDb66nvRXsO9jr
//    gexin.master=rwAcWZzJNk9igejxwmMoi4
    public static void inital(Config cfs) throws IOException {
    	SERVER_PORT=cfs.getAsInteger("server.port");
    	MAX_CONTENT_LENGTH=cfs.getAsInteger("server.reques.context.length");
 //       pid_path = cfs.getAsString("server.pid.path");
        
//        igexin_host = cfs.getAsString("gexin.api.igexin");
//        igexin_master = cfs.getAsString("gexin.master");
//        igexin_appkey = cfs.getAsString("gexin.appkey");
//        igexin_appid = cfs.getAsString("gexin.appid");
    }
}
