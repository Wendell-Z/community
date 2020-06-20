package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


public class SessionCaching {
    /**
     * redis 默认连接工厂
     * 以下作用
     * 1.作为spring session 分布式共享的Nosql数据源
     * 2.作为系统缓存/key值监听等缓存功能RedisTemplate的链接工厂
     *
     * @return
     */
    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }
}
