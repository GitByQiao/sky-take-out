package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfiguration {

    /**
     * 连接redis模板
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建redis模板类");
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置key的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置value的序列化器
        //redisTemplate.setValueSerializer(new StringRedisSerializer());
        //设置redis的连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
