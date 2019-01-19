package com.rabbit.auth.server.controller.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName PermissionInterceptor
 * @Description 权限拦截器
 * @Author LZQ
 * @Date 2019/1/19 21:34
 **/
public class PermissionInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("basePath", getAbsolutelypath(request));
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }
        return super.preHandle(request, response, handler);
    }

    /*获取请求得绝对路径*/
    private String getAbsolutelypath(HttpServletRequest request) {
        String portocol = "";
        if (request.isSecure()) {
            portocol = "https://";
        } else {
            portocol = "http://";
        }
        String absoluteContextPath = portocol + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        return absoluteContextPath;
    }
}
