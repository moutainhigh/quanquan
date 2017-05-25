package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import netty.server.client.NettyClient;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.elastic.Json;
import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.sm.model.SMTask;



/**
 * @author zhoujia
 *
 * @date 2015年7月27日
 */
public class Test {
	
    BowConfig config;
    HttpRequests request;
    //String host = "http://10.2.67.34:8080";
    String host = "http://localhost:8081";
    Logger logger = LoggerFactory.getLogger(Test.class);
	
    @org.junit.Test
    public void testSign() throws IOException {
        final String sign = "/sendmaster/start";
        Map<String,String> map = new HashMap<>();//设置参数
        map.put("param", "config/rules,zhitou.jar");
        CloseableHttpResponse response = NettyClient.post(host+sign,map);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        logger.info("Back From " + host + sign + ":");
        logger.info("BackData : \n\n" + JsonTool.formatJson(builder.toString(), "\t"));
    }
    
    
    public static void main(String[] args) throws Exception {
    	//94351be9a9855aca51b84d5335d1c46a
    	//Redis..initialShard(new Config("config/conf.properties"));
    	// 初始化redis
    	Redis.initial(new Config("config/app.properties"));
    	Redis.initialShard(new Config("config/app.properties"));
    	String string = Redis.shard.get("b19322d32692a790c12bb2ab7dd39e86");
    	SMTask smTask = (SMTask) Json.jsonParser(string, SMTask.class);
    	System.out.println("===="+ smTask.getJarName());
	}
}
