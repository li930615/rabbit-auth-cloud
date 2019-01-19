package com.rabbit.auth.core.entity;

/**
 * @ClassName AuthType
 * @Description 枚举类
 * @Author LZQ
 * @Date 2019/1/19 16:55
 **/
public enum AuthType {

    SSO("SSO"),
    APP("APP"),
    API("API"),
    RABBIT("RABBIT");

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    AuthType(String name) {
        this.name = name;
    }
}
