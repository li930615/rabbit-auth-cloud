package com.rabbit.auth.server.common;

/**
 * @ClassName AuthServiceConst
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/19 16:31
 **/
public class AuthServiceConst {

    /*获取服务器状态码*/
    public static final String SSO_SERVER_CODE(String serverCode) {
        return "rabbit-auth-sso-server-code_" + serverCode;
    }

    /*获取客户端状态码*/
    public static final String SSO_CLIENT_CODE(String clientCode) {
        return "rabbit-auth-sso-client-code_" + clientCode;
    }

    /*获取客户端列表*/
    public static final String SSO_CLIENT_LIST(String serverCode) {
        return "rabbit-auth-sso-client-list_" + serverCode;
    }
}
