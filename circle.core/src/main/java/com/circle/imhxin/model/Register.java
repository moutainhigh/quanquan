package com.circle.imhxin.model;

/**
 * @author Created by cxx on 15-7-24.
 */
public class Register {
    public static final String ENTITIES = "entities";
    public static final String CREATED = "created";
    private String username;
    private String password;
    private String nickname;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
