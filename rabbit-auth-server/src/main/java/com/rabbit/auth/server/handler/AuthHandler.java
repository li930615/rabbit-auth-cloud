package com.rabbit.auth.server.handler;

import com.rabbit.auth.core.entity.Authorization;
import com.rabbit.auth.core.util.CookieUtil;
import com.rabbit.auth.server.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName AuthHandler
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/19 16:51
 **/
public class AuthHandler {

    private static Logger logger = LoggerFactory.getLogger(AuthHandler.class);

    public static Authorization getAuthToken(HttpServletRequest request) {
        /*从请求头中获取token*/
        String jsonWebToken = request.getHeader("Authorization");
        /*如果请求头中没有带token，就从参数中获取*/
        if (StringUtils.isBlank(jsonWebToken)) {
            jsonWebToken = request.getParameter("Authorization");
            logger.debug("{} Parameter get : {}", "Authorization", jsonWebToken);
        }
        /*如果请求头和参数中都没有带token，就从cookie中获取*/
        if (StringUtils.isBlank(jsonWebToken)) {
            jsonWebToken = CookieUtil.getValue(request, "Authorization");
            logger.debug("{} cookie get : {}", "Authorization", jsonWebToken);
        }
        if (StringUtils.isNotBlank(jsonWebToken)){
            return JwtUtil.getAuthorization(jsonWebToken);
        }
        return null;
    }
}
