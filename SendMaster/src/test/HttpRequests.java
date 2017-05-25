package test;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequests {
    public static String POST = "POST";
    public static String GET = "GET";
    private BowConfig config;

    public HttpRequests(BowConfig config) {
        this.config = config;
    }

    public void requestGet(String uri, ConectionDo conndo) throws IOException {
        HttpURLConnection httpConnection = getConnection(uri);
        try {
            httpConnection.setRequestMethod(GET);
            httpConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conndo.doSomething(httpConnection);
        httpConnection.disconnect();
    }

    public void post(String uri, Map<String, String> param,
                     ConectionDo conndo) {
        HttpPost post = new HttpPost(uri);
        List<NameValuePair> nvps = new ArrayList<>();
        for (String key : param.keySet()) {
            String value = param.get(key);
            if (value != null) {
                nvps.add(new BasicNameValuePair(key, value));
            }
        }
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response =client.execute(post);
            conndo.doSomething(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HttpURLConnection getConnection(String urlPath) {
        HttpURLConnection httpCon = null;
        try {
            httpCon = (HttpURLConnection) new URL(urlPath).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.config.setConfig(httpCon);
        return httpCon;
    }
}
