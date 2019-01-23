package com.rabbit.auth.server.controller;

import com.rabbit.auth.core.entity.Authorization;
import com.rabbit.auth.server.handler.AuthHandler;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName AuthController
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/20 15:21
 **/
@Api
@Controller
public class AuthController {

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @ResponseBody
    @RequestMapping(value = "/authorization")
    public ResponseEntity<Object> user(Model model, HttpServletRequest request, HttpServletResponse response) {
        Authorization authorization = AuthHandler.getAuthToken(request);
        if (authorization != null) {
            return new ResponseEntity<Object>(authorization, HttpStatus.OK);
        }
        Map map = new HashMap();
        map.put("message", "Authorization解析失败");
        //返回状态码:400
        return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
    }
}
