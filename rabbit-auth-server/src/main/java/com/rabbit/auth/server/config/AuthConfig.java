package com.rabbit.auth.server.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName AuthConfig
 * @Description 权限配置(这块是从config-server里面拿的东西，可以写在配置文件里application.yml)
 * @Author LZQ
 * @Date 2019/1/19 16:35
 **/
@Configuration
public class AuthConfig implements InitializingBean {

    @Value("${config.area:}")
    public String area;

    @Value("${config.company:}")
    public String company;

    @Value("${config.copyright}")
    public String copyright;

    public void afterPropertiesSet() throws Exception {

    }
}
