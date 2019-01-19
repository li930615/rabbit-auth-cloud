package com.rabbit.auth.core.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @ClassName RedisUtil
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/19 21:01
 **/
public class RedisUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);
    public static String IP;
    public static int PORT;
    public static String PASSWORD;
    public static int MAX_ACTIVE = 1000;

    public static int MAX_IDLE = 1000;

    public static int MAX_WAIT = 10000;

    public static int TIMEOUT = 10000;

    public static boolean TEST_ON_BORROW = false;

    public static JedisPool jedisPool = null;
    public static final int EXRP_MINUTE = 60;
    public static final int EXRP_HOUR = 3600;
    public static final int EXRP_DAY = 86400;
    public static final int EXRP_MONTH = 2592000;

    /*初始化方法*/
    private static void initialPool() {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_ACTIVE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, IP, PORT, TIMEOUT);
        } catch (Exception e) {
            LOGGER.error("First create JedisPool error : " + e);
        }
    }

    private static synchronized void poolInit() {
        if (null == jedisPool)
            initialPool();
    }

    public static synchronized Jedis getJedis() {
        poolInit();
        Jedis jedis = null;
        try {
            if (null != jedisPool) {
                jedis = jedisPool.getResource();
                try {
                    jedis.auth(PASSWORD);
                } catch (Exception localException1) {
                }
            }
        } catch (Exception e) {
            LOGGER.error("Get jedis error : " + e);
        }

        return jedis;
    }

    public static synchronized void set(String key, String value) {
        try {
            value = StringUtils.isBlank(value) ? "" : value;
            Jedis jedis = getJedis();
            jedis.set(key, value);
            jedis.close();
        } catch (Exception e) {
            LOGGER.error("Set key error : " + e);
        }
    }

    public static synchronized void set(byte[] key, byte[] value) {
        try {
            Jedis jedis = getJedis();
            jedis.set(key, value);
            jedis.close();
        } catch (Exception e) {
            LOGGER.error("Set key error : " + e);
        }
    }

    public static synchronized void set(String key, String value, int seconds) {
        try {
            value = StringUtils.isBlank(value) ? "" : value;
            Jedis jedis = getJedis();
            jedis.setex(key, seconds, value);
            jedis.close();
        } catch (Exception e) {
            LOGGER.error("Set keyex error : " + e);
        }
    }

    public static synchronized void set(byte[] key, byte[] value, int seconds) {
        try {
            Jedis jedis = getJedis();
            jedis.set(key, value);
            jedis.expire(key, seconds);
            jedis.close();
        } catch (Exception e) {
            LOGGER.error("Set key error : " + e);
        }
    }

    public static synchronized String get(String key) {
        Jedis jedis = getJedis();
        if (null == jedis) {
            return null;
        }
        String value = jedis.get(key);
        jedis.close();
        return value;
    }

    public static synchronized byte[] get(byte[] key) {
        Jedis jedis = getJedis();
        if (null == jedis) {
            return null;
        }
        byte[] value = jedis.get(key);
        jedis.close();
        return value;
    }

    public static synchronized void remove(String key) {
        try {
            Jedis jedis = getJedis();
            jedis.del(key);
            jedis.close();
        } catch (Exception e) {
            LOGGER.error("Remove keyex error : " + e);
        }
    }

    public static synchronized void remove(byte[] key) {
        try {
            Jedis jedis = getJedis();
            jedis.del(key);
            jedis.close();
        } catch (Exception e) {
            LOGGER.error("Remove keyex error : " + e);
        }
    }

    public static synchronized void lpush(String key, String[] strings) {
        try {
            Jedis jedis = getJedis();
            jedis.lpush(key, strings);
            jedis.close();
        } catch (Exception e) {
            LOGGER.error("lpush error : " + e);
        }
    }

    public static synchronized void lrem(String key, long count, String value) {
        try {
            Jedis jedis = getJedis();
            jedis.lrem(key, count, value);
            jedis.close();
        } catch (Exception e) {
            LOGGER.error("lpush error : " + e);
        }
    }

    public static synchronized void sadd(String key, String value, int seconds) {
        try {
            Jedis jedis = getJedis();
            jedis.sadd(key, new String[]{value});
            jedis.expire(key, seconds);
            jedis.close();
        } catch (Exception e) {
            LOGGER.error("sadd error : " + e);
        }
    }

    public static synchronized Long incr(String key) {
        Jedis jedis = getJedis();
        if (null == jedis) {
            return null;
        }
        long value = jedis.incr(key).longValue();
        jedis.close();
        return Long.valueOf(value);
    }

    public static synchronized Long decr(String key) {
        Jedis jedis = getJedis();
        if (null == jedis) {
            return null;
        }
        long value = jedis.decr(key).longValue();
        jedis.close();
        return Long.valueOf(value);
    }
}
