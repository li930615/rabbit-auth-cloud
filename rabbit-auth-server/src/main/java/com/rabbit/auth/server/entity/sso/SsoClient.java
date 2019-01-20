package com.rabbit.auth.server.entity.sso;

/**
 * @ClassName SsoClient
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/20 11:04
 **/
public class SsoClient {

    private String clientId;
    private String code;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
