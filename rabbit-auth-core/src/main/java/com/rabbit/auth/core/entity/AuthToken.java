package com.rabbit.auth.core.entity;

/**
 * @ClassName AuthToken
 * @Description token对象
 * @Author LZQ
 * @Date 2019/1/19 16:55
 **/
public class AuthToken {

    private AuthType authType;
    private Object data;

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public AuthToken(AuthType authType, Object data) {
        this.authType = authType;
        this.data = data;
    }
}
