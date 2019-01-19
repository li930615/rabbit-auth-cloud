package com.rabbit.auth.core.common;

import com.rabbit.auth.core.entity.R;

/**
 * @ClassName AuthConst
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/19 20:18
 **/
public class AuthConst {

    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String REDIRECT_URL = "redirect_url";
    public static final String AUTH_SERVER = "auth_server";
    public static final String SSO_USER = "sso_user";
    public static final String URL_SSO_LOGIN = "/sso/login";
    public static final String URL_SSO_LOGOUT = "/sso/logout";
    public static final String URL_SSO_TOKEN = "/sso/token";
    public static final String SSO_LOGOUT_PATH = "logoutPath";
    public static final String CODE = "code";
    public static final String AUTHORIZATION = "Authorization";
    public static final String TOKEN_ACCESS = "access_token";
    public static final R<String> SSO_LOGIN_FAIL_RESULT = new R(new Exception("sso not login"));
}
