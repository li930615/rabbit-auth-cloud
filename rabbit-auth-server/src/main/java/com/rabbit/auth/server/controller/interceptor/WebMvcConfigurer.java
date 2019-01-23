package com.rabbit.auth.server.controller.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @ClassName WebMvcConfigurer
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/19 21:39
 **/
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    /*将权限拦截器交给SpringMVC代理*/
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PermissionInterceptor()).addPathPatterns(new String[] { "/**" });
        super.addInterceptors(registry);
    }
}
