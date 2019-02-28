package com.rabbit.auth.server.handler;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.rabbit.auth.core.entity.AuthType;
import com.rabbit.auth.core.entity.Authorization;
import com.rabbit.auth.core.util.CookieUtil;
import com.rabbit.auth.server.common.AuthServiceConst;
import com.rabbit.auth.server.entity.sso.SsoClient;
import com.rabbit.auth.server.entity.sso.SsoToken;
import com.rabbit.auth.server.util.JwtUtil;
import com.rabbit.common.entity.CurrentUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName SsoHandler
 * @Description 登录授权处理器
 * @Author LZQ
 * @Date 2019/1/20 11:18
 **/
public class SsoHandler {

    @Autowired
    public static StringRedisTemplate stringRedisTemplate;

    private static Logger logger = LoggerFactory.getLogger(SsoHandler.class);

    /*登录验证*/
    public static boolean loginCheck(HttpServletRequest request, HttpServletResponse response, String serverCode) {
        //如果登录验证中没有携带服务信息，则从该请求中移除认证
        if ((StringUtils.isBlank(serverCode)) || (!verifySsoTokenServer(serverCode))) {
            CookieUtil.remove(request, response, "Authorization");
            return false;
        }
        return true;
    }

    /*从请求中获取与key(Authorization)对应的认证信息*/
    public static String getAuthorization(HttpServletRequest request) {
        //从请求头中获取认证信息
        String jwt = request.getHeader("Authorization");
        if (StringUtils.isBlank(jwt)) {
            //根据请求和key从Cookie中获取认证信息token
            jwt = CookieUtil.getValue(request, "Authorization");
            logger.debug("{} cookie get : {}", "Authorization", jwt);
        }
        if (StringUtils.isBlank(jwt)) {
            //从request请求携带的参数中获取认证信息token
            jwt = request.getParameter("Authorization");
            logger.debug("{} parm get : {}", "Authorization", jwt);
        }
        return jwt;
    }

    /*根据token生成获取认证信息*/
    public static SsoToken getSsoToken(String token) {
        SsoToken ssoToken = null;
        if (StringUtils.isNotBlank(token)) {
            Authorization authorization = JwtUtil.getAuthorization(token);
            return getSsoToken(authorization);
        }
        return ssoToken;
    }

    /*根据认证信息获取token*/
    public static SsoToken getSsoToken(Authorization authorization) {
        SsoToken ssoToken = null;
        if (authorization != null) {
            //根据认证信息获取认证类型
            AuthType authType = authorization.getAuthType();
            //如果是登录请求就返回一个登录的token
            if (authType == AuthType.SSO) {
                ssoToken = new Gson().fromJson(authorization.getAuthToken().toString(), SsoToken.class);
            }
            logger.debug("{} SsoToken : {}", "Authorization", ssoToken);
        }
        return ssoToken;
    }

    /*先从请求中获取与key(Authorization)对应的认证信息*/
    public static SsoToken getSsoToken(HttpServletRequest request) {
        return getSsoToken(getAuthorization(request));
    }

    /*退出登录操作*/
    public static boolean logout(HttpServletRequest request, HttpServletResponse response) {
        String serverCode = null;
        SsoToken ssoToken = getSsoToken(request);
        if (ssoToken != null) {
            //获取服务状态码
            serverCode = ssoToken.getCode();
        }
        if (StringUtils.isNotBlank(serverCode)) {
            logger.info("del sso server code : {}", serverCode);
            //从redis中剔除该服务
            stringRedisTemplate.delete(AuthServiceConst.SSO_SERVER_CODE(serverCode));
            //根据key()获取set()集合
            Set<String> ssoClientJsonSet = stringRedisTemplate.opsForSet().members(AuthServiceConst.SSO_CLIENT_LIST(serverCode));
            for (String ssoClientJson : ssoClientJsonSet) {
                SsoClient ssoClient = JSON.parseObject(ssoClientJson, SsoClient.class);
                //从redis中剔除该客户端
                stringRedisTemplate.delete(AuthServiceConst.SSO_CLIENT_CODE(ssoClient.getCode()));
                logger.info("del sso client[{}] code : [{}]", ssoClient.getClientId(), ssoClient.getCode());
            }
            stringRedisTemplate.delete(AuthServiceConst.SSO_CLIENT_LIST(serverCode));

            CookieUtil.remove(request, response, serverCode);
            return true;
        }
        return false;
    }

    /*缓存登录信息*/
    public static boolean saveServerCode(HttpServletResponse response, String serverCode, CurrentUser currentUser) {
        try {
            logger.info("new sso server code : {}", serverCode);
            //向redis里存入登录服务码和当前登录的用户并设置缓存时间
            System.out.println(AuthServiceConst.SSO_SERVER_CODE(serverCode));
            System.out.println(JSON.toJSONString(currentUser));
            stringRedisTemplate.opsForValue().set( AuthServiceConst.SSO_SERVER_CODE(serverCode), "AAAAA", 86400L, TimeUnit.SECONDS);
            //生成登录的token
            SsoToken ssoToken = new SsoToken("1", serverCode);
            //加入当前请求用户的信息
            ssoToken.setUser(currentUser);
            //生成token
            String jwt = JwtUtil.createJWT(new Authorization(AuthType.SSO, ssoToken));
            //向cookie中加入认证信息token
            CookieUtil.set(response, "Authorization", jwt, true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getClientCode(String clientId, String serverCode) {
        String clientCode = null;
        //根据key(服务名)获取redis中的值
        if (stringRedisTemplate.opsForValue().get(AuthServiceConst.SSO_SERVER_CODE(serverCode)) != null) {
            //根据客户端列表key获取所有的客户端值
            Set<String> ssoClientJsonSet = stringRedisTemplate.opsForSet().members(AuthServiceConst.SSO_CLIENT_LIST(serverCode));
            if (ssoClientJsonSet != null) {
                //获取客户端列表
                for (String ssoClientJson : ssoClientJsonSet) {
                    SsoClient ssoClient = JSON.parseObject(ssoClientJson, SsoClient.class);
                    if (ssoClient.getClientId().equals(clientId)) {
                        clientCode = ssoClient.getCode();
                        break;
                    }
                }
            }

            if (clientCode == null) {
                clientCode = UUID.randomUUID().toString();
            }
            //向redis里存入数据并设置缓存时间
            stringRedisTemplate.opsForValue().set(AuthServiceConst.SSO_CLIENT_CODE(clientCode), serverCode, 60L, TimeUnit.SECONDS);
        }
        return clientCode;
    }

    /*根据客户端ID和客户端名称获取客户端token*/
    public static String getClientToken(String clientId, String clientCode) {
        String token = null;
        if ((StringUtils.isBlank(clientId)) || (StringUtils.isBlank(clientCode))) {
            return null;
        }
        //根据客户端名称获取对应服务端名称
        String serverCode = stringRedisTemplate.opsForValue().get(AuthServiceConst.SSO_CLIENT_CODE(clientCode));
        if (serverCode != null) {
            //根据服务端名称获取用户的信息
            String ssoUserJson = stringRedisTemplate.opsForValue().get(AuthServiceConst.SSO_SERVER_CODE(serverCode));
            if (ssoUserJson != null) {
                //解析用户信息
                CurrentUser currentUser = JSON.parseObject(ssoUserJson, CurrentUser.class);
                //生成客户端登录所需token
                SsoToken ssoToken = new SsoToken("2", clientCode);
                ssoToken.setUser(currentUser);
                token = JwtUtil.createJWT(new Authorization(AuthType.SSO, ssoToken));

                SsoClient ssoClient = new SsoClient();
                ssoClient.setClientId(clientId);
                ssoClient.setCode(clientCode);
                //向登录列表中放入登录信息
                stringRedisTemplate.opsForSet().add(AuthServiceConst.SSO_CLIENT_LIST(serverCode), new String[]{JSON.toJSONString(ssoClient)});
                //设置过期时间
                stringRedisTemplate.expire(AuthServiceConst.SSO_CLIENT_LIST(serverCode), 3600L, TimeUnit.SECONDS);
                stringRedisTemplate.expire(AuthServiceConst.SSO_SERVER_CODE(serverCode), 3600L, TimeUnit.SECONDS);
                stringRedisTemplate.expire(AuthServiceConst.SSO_CLIENT_CODE(clientCode), 3600L, TimeUnit.SECONDS);
            }
        }
        return token;
    }

    /*验证客户端登录token*/
    public static boolean verifySsoTokenClient(String clientCode) {
        if (StringUtils.isBlank(clientCode)) {
            return false;
        }
        //检查该客户端认证是否失效
        Long expire = stringRedisTemplate.boundHashOps(AuthServiceConst.SSO_CLIENT_CODE(clientCode)).getExpire();

        if (-2L == expire.longValue()) {
            return false;
        }
        //如果认证未失效，重新设置缓存时间
        if (expire.longValue() < 3300L) {
            stringRedisTemplate.expire(AuthServiceConst.SSO_CLIENT_CODE(clientCode), 3600L, TimeUnit.SECONDS);
        }

        String serverCode = stringRedisTemplate.opsForValue().get(AuthServiceConst.SSO_CLIENT_CODE(clientCode));
        return verifySsoTokenServer(serverCode);
    }

    public static boolean verifySsoTokenServer(String serverCode) {
        if (StringUtils.isBlank(serverCode)) {
            return false;
        }
        //将服务key存入redis中，并根据serverCode获取过期时间
        Long expire = stringRedisTemplate.boundHashOps(AuthServiceConst.SSO_SERVER_CODE(serverCode)).getExpire();

        if (-2L == expire.longValue()) {
            return false;
        }
        if (expire.longValue() < 3600L) {
            //设置服务的过期时间
            stringRedisTemplate.expire(AuthServiceConst.SSO_SERVER_CODE(serverCode), 3600L, TimeUnit.SECONDS);
            //设置服务列表的过期时间
            stringRedisTemplate.expire(AuthServiceConst.SSO_CLIENT_LIST(serverCode), 3600L, TimeUnit.SECONDS);
        }
        return true;
    }

    /*登录token验证*/
    public static boolean verifySsoToken(SsoToken ssoToken)
    {
        if (ssoToken == null) {
            return false;
        }
        //获取登录方式，是客户端登录还是服务端登录
        String ssoType = ssoToken.getSsoType();
        String code = ssoToken.getCode();
        //如果是服务端登录
        if ("1".equals(ssoType)) {
            return verifySsoTokenServer(code);
        }
        return verifySsoTokenClient(code);
    }
}
