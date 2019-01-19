package com.rabbit.auth.core.entity;

import java.io.Serializable;

/**
 * @ClassName Authorization
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/19 16:54
 **/
public class Authorization implements Serializable {

    private AuthType authType;
    private Object authToken;

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public Object getAuthToken() {
        return authToken;
    }

    public void setAuthToken(Object authToken) {
        this.authToken = authToken;
    }

    public Authorization(AuthType authType, Object authToken) {
        this.authType = authType;
        this.authToken = authToken;
    }
}
