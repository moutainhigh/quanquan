package com.circle.imhxin.model;

/**
 * 请求环信 管理员token
 * @author Created by cxx on 15-7-24.
 */
public class HuanXinToken {
    public static final String access_token = "access_token";
    public static final String expires_in = "expires_in";
    public static final String application = "application";

    private String grant_type;
    private String client_id;
    private String client_secret;

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public HuanXinToken() {
    }

    public static class ResModel{
        private String access_token;
        private Long expires_in;
        private String application;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public Long getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(Long expires_in) {
            this.expires_in = expires_in;
        }

        public String getApplication() {
            return application;
        }

        public void setApplication(String application) {
            this.application = application;
        }
    }
}
