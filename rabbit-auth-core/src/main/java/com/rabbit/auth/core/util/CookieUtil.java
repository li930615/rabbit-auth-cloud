package com.rabbit.auth.core.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName CookieUtil
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/19 17:12
 **/
public class CookieUtil {

    private static final int COOKIE_MAX_AGE = 3600;//cookie有效时间
    private static final String COOKIE_PATH = "/";

    /*给请求的响应中的cookie放入信息：并设置过期时间（正数代表cookie的过期时间，负数表示该Cookie仅在本浏览器窗口以及本窗口打开的子窗口内有效
    ，关闭窗口后该Cookie即失效。为0表示表示删除该Cookie。Cookie机制没有提供删除Cookie的方法，因此通过设置该Cookie即时失效实现删除Cookie的效果。
    失效的Cookie会被浏览器从Cookie文件或者内存中删除）*/
    public static void set(HttpServletResponse response, String key, String value, boolean ifRemember) {
        int age = ifRemember ? 3600 : -1;
        set(response, key, value, null, "/", age, true);
    }

    private static void set(HttpServletResponse response, String key, String value, String domain, String path, int maxAge, boolean isHttpOnly) {
        Cookie cookie = new Cookie(key, value);
        if (domain != null) {
            cookie.setDomain(domain);
        }
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(isHttpOnly);
        response.addCookie(cookie);
    }

    /*从Cookie中获取认证信息token*/
    public static String getValue(HttpServletRequest request, String key) {
        Cookie cookie = get(request, key);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    private static Cookie get(HttpServletRequest request, String key) {
        Cookie[] arr_cookie = request.getCookies();
        if ((arr_cookie != null) && (arr_cookie.length > 0)) {
            for (Cookie cookie : arr_cookie) {
                if (cookie.getName().equals(key)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static void remove(HttpServletRequest request, HttpServletResponse response, String key) {
        Cookie cookie = get(request, key);
        if (cookie != null)
            set(response, key, "", null, "/", 0, true);
    }

}
