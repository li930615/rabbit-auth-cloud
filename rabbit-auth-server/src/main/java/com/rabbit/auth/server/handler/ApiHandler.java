package com.rabbit.auth.server.handler;

import com.rabbit.auth.core.entity.AuthType;
import com.rabbit.auth.core.entity.Authorization;
import com.rabbit.auth.server.entity.api.ApiToken;
import com.rabbit.auth.server.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @ClassName ApiHandler
 * @Description 第三方应用授权处理器（根据请求得需要得权限类型生成对应得token）
 * @Author LZQ
 * @Date 2019/1/20 11:09
 **/
public class ApiHandler {

    public static StringRedisTemplate stringRedisTemplate;

    private static Logger logger = LoggerFactory.getLogger(ApiHandler.class);

    public static String getApiToken(String appId, String appSecret) {
        String apiToken = null;
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appSecret)) {
            return null;
        }
        /*根据权限类型生成接口token*/
        apiToken = JwtUtil.createJWT(new Authorization(AuthType.API, new ApiToken(appId, appSecret)));
        return apiToken;
    }
}
