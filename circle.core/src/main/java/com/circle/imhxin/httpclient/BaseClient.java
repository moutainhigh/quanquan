package com.circle.imhxin.httpclient;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Created by cxx on 15-7-23.
 */
@SuppressWarnings("unused")
public class BaseClient {
    public static Header CONTEXT_JSON = new BasicHeader("Content-Type", "application/json");
    /**
     * 多线程执行..
     */
    public HttpClientBuilder builder = HttpClientBuilder.create();

    /**
     * 发送 Post 请求
     *
     * @param uri   接口地址
     * @param param 请求参数
     * @return 返回服务器返回流
     * @throws IOException
     */
    public CloseableHttpResponse post(String uri, Map<String, String> param) throws IOException {
        return post(uri, param, null);
    }
    public HttpPost createpost(String uri, Map<String, String> param) throws IOException {
        return createpost(uri, param, null);
    }

    /**
     * 发送 Post 请求
     *
     * @param uri   接口地址
     * @param param 请求参数
     * @return 返回服务器返回流
     * @throws IOException
     */
    public CloseableHttpResponse post(String uri, Map<String, String> param, Header[] headers) throws IOException {
        HttpPost post = new HttpPost(uri);
        List<NameValuePair> nvps = new ArrayList<>();
        if (param != null)
            for (String key : param.keySet()) {
                String value = param.get(key);
                if (value != null) {
                    nvps.add(new BasicNameValuePair(key, new String(value.getBytes("UTF-8"), "ISO-8859-1")));
                }
            }
        if (headers != null) {
            post.setHeaders(headers);
        }
        post.removeHeaders(HTTP.CONTENT_ENCODING);
        post.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpClient client = builder.build();
        return client.execute(post);
    }
    public HttpPost createpost(String uri, Map<String, String> param, Header[] headers) throws IOException {
        HttpPost post = new HttpPost(uri);
        List<NameValuePair> nvps = new ArrayList<>();
        if (param != null)
            for (String key : param.keySet()) {
                String value = param.get(key);
                if (value != null) {
                    nvps.add(new BasicNameValuePair(key, new String(value.getBytes("UTF-8"), "ISO-8859-1")));
                }
            }
        if (headers != null) {
            post.setHeaders(headers);
        }
        post.setEntity(new UrlEncodedFormEntity(nvps));
        return post;
    }

    public CloseableHttpResponse postSsl(String uri, String json, Header[] headers) throws IOException {
        HttpPost post = new HttpPost(uri);
        if (headers != null) {
            post.setHeaders(headers);
        }
        post.addHeader(CONTEXT_JSON);
        if (json != null){
            StringEntity entity = new StringEntity(new String(json.getBytes("UTF-8"), "ISO-8859-1"));
            post.setEntity(entity);
        }
        CloseableHttpClient client = getClient(true);
        return client.execute(post);
    }
    public CloseableHttpResponse putSsl(String uri, String json, Header[] headers) throws IOException {
        HttpPut httpPut = new HttpPut(uri);
        if (headers != null) {
            httpPut.setHeaders(headers);
        }
        //post.addHeader(CONTEXT_JSON);
        if (json != null){
            StringEntity entity = new StringEntity(new String(json.getBytes("UTF-8"), "ISO-8859-1"));
            httpPut.setEntity(entity);
        }
        CloseableHttpClient client = getClient(true);
        return client.execute(httpPut);
    }

    /**
     * 发送 get 请求
     *
     * @param url 请求地址
     * @return 返回, 服务器返回
     * @throws IOException
     */
    public CloseableHttpResponse get(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        CloseableHttpClient client = builder.build();
        return client.execute(get);
    }

    public CloseableHttpClient getClient(boolean isSSL) {
        CloseableHttpClient httpClient = builder.build();
        if (isSSL) {
            X509TrustManager xtm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            try {
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, new TrustManager[]{xtm}, null);
                SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(ctx);
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx);
                builder.setSSLSocketFactory(socketFactory);
                //httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
        return httpClient;
    }


}
