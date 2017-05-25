package netty.server.client;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Created by cxx on 15-7-23.
 */
public class NettyClient {
    /**
     * 多线程执行..
     */
    private static  HttpClientBuilder builder = HttpClientBuilder.create();

    public static CloseableHttpResponse post(String uri, Map<String, String> param) throws IOException {
        HttpPost post = new HttpPost(uri);
        List<NameValuePair> nvps = new ArrayList<>();
        for (String key : param.keySet()) {
            String value = param.get(key);
            if (value != null) {
                nvps.add(new BasicNameValuePair(key, value));
            }
        }
        post.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpClient client = builder.build();
        CloseableHttpResponse response = client.execute(post);
        return response;
    }
}
