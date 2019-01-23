package com.rabbit.auth.server.controller;

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
 * @ClassName AppController
 * @Description 应用模块登录接口
 * @Author LZQ
 * @Date 2019/1/20 14:09
 **/
@Api(value = "应用模块登录控制器", tags = {"APP登录接口"})
@Controller
public class AppController {

    private static Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @RequestMapping(value = "/application/login")
    @ApiOperation(value = "应用模块登录接口", httpMethod = "POST")
    /*用在请求的方法上，表示一组参数说明*/
    @ApiImplicitParams({@io.swagger.annotations.ApiImplicitParam(name = "login_name", value = "用户名", required = true), @io.swagger.annotations.ApiImplicitParam(name = "password", value = "密码", required = true)})
    public ResponseEntity<Map<String, Object>> apiToken(String login_name, String password, String token) {
        Map map = new HashMap();
        if (StringUtils.isNotBlank(token)){
            logger.info("app [{}] login auth token [{}] ", login_name, token);
            map.put("access_token", token);
            return new ResponseEntity(map, HttpStatus.OK);
        }
        map.put("error", "Bad Request");
        map.put("message", "token获取失败");
        return new ResponseEntity(map, HttpStatus.BAD_REQUEST);
    }

}
