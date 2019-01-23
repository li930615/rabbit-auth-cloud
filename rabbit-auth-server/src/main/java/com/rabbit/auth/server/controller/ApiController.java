package com.rabbit.auth.server.controller;

import com.rabbit.auth.server.handler.ApiHandler;
import com.rabbit.feign.ucenter.model.entity.SysLicense;
import com.rabbit.feign.ucenter.server.SysLicenseApiServer;
import com.rabbit.feign.ucenter.server.SysUserServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ApiController
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/21 19:27
 **/
@Api(value = "API 授权 controller", tags = {"第三方应用接入授权接口"})
@Controller
public class ApiController {

    private static Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    SysUserServer sysUserServer;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    SysLicenseApiServer sysLicenseApiServer;

    @RequestMapping(value = "/api/token")
    @ApiOperation(value = "应用获取API token")
    @ApiImplicitParams({@io.swagger.annotations.ApiImplicitParam(name = "app_id", value = "客户端ID", required = true), @io.swagger.annotations.ApiImplicitParam(name = "app_secret", value = "客户端密钥", required = true)})
    public ResponseEntity<Map<String, Object>> apiToken(String app_id, String app_secret) {
        Map map = new HashMap();
        if (StringUtils.isBlank(app_id)) {
            map.put("error", "Bad Request");
            map.put("message", "应用id为空");
            return new ResponseEntity(map, HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(app_secret)) {
            map.put("error", "Bad Request");
            map.put("message", "应用密钥为空");
            return new ResponseEntity(map, HttpStatus.BAD_REQUEST);
        }
        SysLicense sysLicense = this.sysLicenseApiServer.getLicenseByAppId(app_id);
        if (sysLicense == null) {
            map.put("error", "Bad Request");
            map.put("message", "应用id未授权");
            return new ResponseEntity(map, HttpStatus.BAD_REQUEST);
        }
        if ((sysLicense != null) && (StringUtils.isBlank(sysLicense.getAppSecret()))) {
            map.put("error", "Bad Request");
            map.put("message", "应用id未授权");
            return new ResponseEntity(map, HttpStatus.BAD_REQUEST);
        }
        if (sysLicense.getAppSecret().equals(app_secret)) {
            String token = ApiHandler.getApiToken(app_id, app_secret);
            if (StringUtils.isNotBlank(token)) {
                logger.info("api [{}] auth token [{}] ", app_id, token);
                map.put("access_token", token);
                return new ResponseEntity(map, HttpStatus.OK);
            }
            map.put("error", "Bad Request");
            map.put("message", "token获取失败");
            return new ResponseEntity(map, HttpStatus.BAD_REQUEST);
        }

        map.put("error", "Bad Request");
        map.put("message", "应用密钥和应用id不相符");
        return new ResponseEntity(map, HttpStatus.BAD_REQUEST);
    }
}

