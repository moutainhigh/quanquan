package com.circle.imhxin.service;

import com.circle.core.elastic.Json;
import com.circle.imhxin.httpclient.BaseClient;
import com.circle.imhxin.httpclient.HxAdminConfig;
import com.circle.imhxin.model.HuanXinToken;
import com.circle.imhxin.model.Register;
import com.circle.imhxin.model.SystemMessage;
import com.circle.imhxin.model.type.GrantType;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by cxx on 15-7-24.
 */
@SuppressWarnings("unused")
public class MsgService {
    private static final String token_key = "Authorization";
    private static final String token_prefix = "Bearer ";
    private static final String ERROR = "error";
    private static final String USERS = "users";
    private static final String PASSWORD = "/password";
    private static final String URL_SP = "/";
    private static final String NICKNAME = "nickname";
    private static final String NEWPASSWORD = "newpassword";
    public static MsgService msgService;
    public long invalid;//token失效时间
    public Header[] headers;//设置Token
    public BaseClient client;
    private static Logger logger = LoggerFactory.getLogger(MsgService.class);

    private MsgService() {
        client = new BaseClient();
    }

    /**
     * 单例获取操作类,初始化用.
     *
     * @return
     */
    public static MsgService create() {
        if (msgService == null) {
            msgService = new MsgService();
        }

        return msgService;
    }

    /**
     * 用户
     *
     * @param register
     */
    public boolean register(Register register) throws IOException {

        CloseableHttpResponse response = client.postSsl(HxAdminConfig.create().url_register, Json.json(register), headers);
        JsonNode json = jsonNode(response);
        //判断错误
        JsonNode error = json.get(ERROR);
        if (error != null) {
            logger.error(error.asText());
            return false;
        }
        JsonNode entities = json.get(Register.ENTITIES);
        if (entities != null) {
            JsonNode entity = entities.get(0);
            Long created = entity.get(Register.CREATED).asLong();
        }
        return true;
    }


    public boolean users(String users) throws IOException {
        CloseableHttpResponse response = client.postSsl(HxAdminConfig.create().url_users + "/" + users, null, headers);
        JsonNode node = jsonNode(response);
        logger.info(node.toString());
        return false;
    }


    /**
     * 修改环信密码
     */
    public boolean repasswd(String mobile, String newpwd) throws IOException {
        String url = HxAdminConfig.create().url_base+USERS + URL_SP + mobile + PASSWORD;
        Map<String,String> parameters = new HashMap<>(1);
        parameters.put(NEWPASSWORD, newpwd);
        System.out.println(Json.json(parameters));
        CloseableHttpResponse response = client.putSsl(url, Json.json(parameters), headers);
        String json = stringFrom(response);
        JsonNode node = Json.jsonParser(json);
        JsonNode error = node.get(ERROR);
        if (error != null) {
            logger.error(error.asText());
            return false;
        }
        return true;
    }

    /**
     * 修改环信昵称
     */
    public boolean updateNickname(String username, String nickname) throws IOException {
        String url = HxAdminConfig.create().url_base+USERS + URL_SP + username;
        Map<String,String> parameters = new HashMap<>(1);
        parameters.put(NICKNAME, nickname);
        System.out.println(Json.json(parameters));
        CloseableHttpResponse response = client.putSsl(url, Json.json(parameters), headers);
        String json = stringFrom(response);
        JsonNode node = Json.jsonParser(json);
        JsonNode error = node.get(ERROR);
        if (error != null) {
            logger.error(error.asText());
            return false;
        }
        return true;
    }
    public boolean sendMessage(SystemMessage message) throws IOException {
        System.out.println(Json.json(message));
        CloseableHttpResponse response = client.putSsl(HxAdminConfig.create().url_messages, Json.json(message), headers);
        String json = stringFrom(response);
        JsonNode node = Json.jsonParser(json);
        JsonNode error = node.get(ERROR);
        if (error != null) {
            logger.error(error.asText());
            return false;
        }
        return true;
    }

    public boolean updateToken() throws IOException {
        //更新管理员token
        HuanXinToken token = new HuanXinToken();
        token.setClient_id(HxAdminConfig.create().client_id);
        token.setClient_secret(HxAdminConfig.create().client_secret);
        token.setGrant_type(GrantType.client_credentials);
        CloseableHttpResponse response = client.postSsl(HxAdminConfig.create().url_token, Json.json(token), null);
        String json = stringFrom(response);
        JsonNode node = Json.jsonParser(json);
        JsonNode error = node.get(ERROR);
        if (error != null) {
            logger.error(error.asText());
            return false;
        }
        HuanXinToken.ResModel model = new HuanXinToken.ResModel();
        model.setAccess_token(node.get(HuanXinToken.access_token).asText());
        model.setExpires_in(node.get(HuanXinToken.expires_in).asLong());
        model.setApplication(node.get(HuanXinToken.application).asText());
        setTokenHeader(model.getAccess_token(), model.getExpires_in());
        return true;
    }

    private String stringFrom(CloseableHttpResponse response) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

    private JsonNode jsonNode(CloseableHttpResponse response) throws IOException {
        return Json.jsonParser(stringFrom(response));
    }

    /**
     * 单例获取操作类
     */
    public static MsgService resource() {
        if (msgService.invalid <= System.currentTimeMillis()) {
            try {
                msgService.updateToken();
            } catch (IOException e) {
                logger.error("update Token error", e);
            }
        }
        return msgService;
    }

    /**
     * 更新设置token.
     *
     * @param token 环信管理员token
     * @param time  过期时间
     */
    public void setTokenHeader(String token, long time) {
        if (headers == null)
            headers = new Header[1];
        //更新请求头
        headers[0] = new BasicHeader(token_key, token_prefix + token);
        //设置过期时间
        invalid = System.currentTimeMillis() + time;
    }
}
