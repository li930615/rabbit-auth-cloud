package com.rabbit.auth.server.entity.api;

import java.util.Date;

/**
 * @ClassName ApiToken
 * @Description api接口token对象
 * @Author LZQ
 * @Date 2019/1/20 10:55
 **/
public class ApiToken {

    private String appId;
    private String appSecret;
    private Date createDate;
    private String checkType;

    public ApiToken(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.createDate = new Date();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }
}
