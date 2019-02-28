package com.rabbit.auth.server.util;

import com.google.gson.Gson;
import com.rabbit.auth.core.entity.AuthType;
import com.rabbit.auth.core.entity.Authorization;
import com.rabbit.common.constant.SecurityConst;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;

/**
 * @ClassName JwtUtil
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/19 17:36
 **/
public class JwtUtil {

    public static String createJWT(Authorization authorization) {

        /*HS256加密*/
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("rabbit-auth");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder().setHeaderParam("type", "JWT")
                .claim("auth_type", authorization.getAuthType())
                .claim("auth_token", authorization.getAuthToken())
                .signWith(signatureAlgorithm, signingKey);
        return builder.compact();
    }

    /*解析token中携带的信息获取认证信息*/
    public static Authorization getAuthorization(String token) {
        Claims claims = SecurityConst.parseJWT(token);
        if (claims != null) {
            AuthType authType = new Gson().fromJson(claims.get("auth_type").toString(), AuthType.class);
            Object object = claims.get("auth_token");
            /*根据权限类型和token信息去获取认证*/
            Authorization authorization = new Authorization(authType, object);
            return authorization;
        }
        return null;
    }
}
