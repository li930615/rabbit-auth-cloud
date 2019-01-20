package com.rabbit.auth.server.entity.sso;

import com.rabbit.common.entity.CurrentUser;

/**
 * @ClassName SsoToken
 * @Description 单点登录token对象
 * @Author LZQ
 * @Date 2019/1/20 11:04
 **/
public class SsoToken {

    private String ssoType;
    private String code;
    private CurrentUser user;
    public static final String SSOTYPE_SERVER = "1";
    public static final String SSOTYPE_CLIETNT = "2";

    public SsoToken(String ssoType, String code) {
        this.ssoType = ssoType;
        this.code = code;
    }

    public String getSsoType() {
        return ssoType;
    }

    public void setSsoType(String ssoType) {
        this.ssoType = ssoType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CurrentUser getUser() {
        return user;
    }

    public void setUser(CurrentUser user) {
        this.user = user;
    }
}
