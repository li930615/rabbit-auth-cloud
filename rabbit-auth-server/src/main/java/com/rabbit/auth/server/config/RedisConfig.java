package com.rabbit.auth.server.config;

import com.rabbit.auth.server.handler.ApiHandler;
import com.rabbit.auth.server.handler.SsoHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @ClassName RedisConfig
 * @Description Redis缓存配置
 * @Author LZQ
 * @Date 2019/1/19 16:39
 **/
@Configuration
@EnableCaching
public class RedisConfig implements InitializingBean {

    /*redis过期时间设置，单位：秒*/
    public static final int EXRP_MINUTE = 60;
    public static final int EXRP_HOUR = 3600;
    public static final int EXRP_DAY = 86400;
    public static final int EXRP_MONTH = 2592000;

    /*当你的redis数据库里面本来存的是字符串数据或者你要存取的数据就是字符串类型数据的时候，
      那么你就使用StringRedisTemplate即可。但是如果你的数据是复杂的对象类型，而取出的时候
      又不想做任何的数据转换，直接从Redis里面取出一个对象，那么使用RedisTemplate是更好的选择*/
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    /*配置缓存管理*/
    public CacheManager cacheManager(RedisTemplate<?,?> redisTemplate){
        return new RedisCacheManager(redisTemplate);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory){
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(factory);
        return stringRedisTemplate;
    }

    public void afterPropertiesSet() throws Exception {
        SsoHandler.stringRedisTemplate = this.stringRedisTemplate;
        ApiHandler.stringRedisTemplate = this.stringRedisTemplate;
    }
}
