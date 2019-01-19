package com.rabbit.auth.server.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @ClassName WebConfig
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/19 21:15
 **/
@Configuration
@EnableWebMvc
@ComponentScan//开启组件扫描，交给Spring去管理
public class WebConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler(new String[] { "/static/**" }).addResourceLocations(new String[] { "classpath:/static/" });
        registry.addResourceHandler(new String[] { "/templates/**" }).addResourceLocations(new String[] { "classpath:/templates/" });
        super.addResourceHandlers(registry);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(new MyInterceptor()).addPathPatterns(new String[] { "/**" }).excludePathPatterns(new String[] { "/rpc/**" });
        super.addInterceptors(registry);
    }

    @Bean
    public HttpMessageConverter<String> responseBodyConverter()
    {
        return new StringHttpMessageConverter(Charset.forName("UTF-8"));
    }

    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        converters.add(responseBodyConverter());
    }

    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }
}
