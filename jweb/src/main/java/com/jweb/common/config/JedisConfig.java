package com.jweb.common.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jweb.common.util.StringUtil;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
* @ClassName: JedisConfig 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author liyz liyz@bzhcloud.com 
* @date 2017年9月5日 上午11:26:55 
*
 */
@Configuration
public class JedisConfig {
	@Value("${spring.redis.host:localhost}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.timeout:0}")
    private int timeout;

    @Value("${spring.redis.pool.max-idle:8}")
    private int maxIdle;

    @Value("${spring.redis.pool.max-wait:-1}")
    private long maxWaitMillis;

    @Value("${spring.redis.password:}")
    private String password;

    @Bean
    public JedisPool redisPoolFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);

        JedisPool jedisPool = null;
        if(StringUtil.notNull(password)) {
        	jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
        }else {
        	jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);
        }

        return jedisPool;
    }
}

