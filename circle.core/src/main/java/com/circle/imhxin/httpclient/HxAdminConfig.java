package com.circle.imhxin.httpclient;

import com.circle.core.util.Config;

import java.io.IOException;

/**
 * @author Created by cxx on 15-7-24.
 */
@SuppressWarnings("unused")
public class HxAdminConfig {
    private static final String default_config = "config/huanxin.properties";
    private static HxAdminConfig config;
    public String client_id;
    public String client_secret;
    public String org_name;
    public String app_name;

    public String url_base;
    public String url_token;
    public String url_register;
    public String url_messages;
    public String url_users;
    public static final String url_password = "password";

    private HxAdminConfig(Config config) {
        client_id = config.getAsString("hx.client.id");
        client_secret = config.getAsString("hx.client.secret");
        org_name = config.getAsString("hx.company");
        app_name = config.getAsString("hx.appname");
        url_base = config.getAsString("hx.url.base") + org_name + "/" + app_name + "/";
        url_token = url_base + config.getAsString("hx.url.token");
        url_register = url_base + config.getAsString("hx.url.register");
        url_messages = url_base + config.getAsString("hx.url.messages");
        url_messages = url_base + config.getAsString("hx.url.messages");
        url_users = url_base + config.getAsString("hx.url.users");
    }

    public static HxAdminConfig create() throws IOException {
        if (config == null)
            config = new HxAdminConfig(new Config(default_config));
        return config;
    }

    public static HxAdminConfig create(Config confg) throws IOException {
        return config = new HxAdminConfig(confg);
    }
}
