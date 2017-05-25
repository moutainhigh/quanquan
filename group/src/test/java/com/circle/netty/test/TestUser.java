package com.circle.netty.test;

import com.circle.core.elastic.Json;
import com.circle.core.redis.Redis;
import com.circle.netty.http.client.NettyClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.fomky.browser.core.BowConfig;
import org.fomky.browser.core.ConectionDo;
import org.fomky.browser.core.HttpRequests;
import org.fomky.browser.core.JsonTool;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关接口测试类
 *
 * @author Created by cxx on 7/20/15.
 */
public class TestUser {
    BowConfig config;
    HttpRequests request;
    //String host = "http://10.2.67.34:8080";
    String host = "http://localhost:8081";
    Logger logger = LoggerFactory.getLogger(TestUser.class);
    @Before
    public void testBefore() {
        config = new BowConfig("config/bow.properties");
        request = new HttpRequests(config);
    }

    @Test
    public void testSign() throws IOException {
        final String sign = "/user/sign";
        Map<String,String> map = new HashMap<>();//设置参数
        map.put("key", "value");
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

    @Test
    public void json(){
        String json = "{\"a\":11}";
        JsonNode node = Json.jsonParser(json);
        System.out.println(Json.json(node));
    }

}
