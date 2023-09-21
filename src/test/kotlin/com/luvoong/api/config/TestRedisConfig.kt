package com.luvoong.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
class TestRedisConfig(
    private val testRedisContainerConfig: TestRedisContainerConfig
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(testRedisContainerConfig.host, testRedisContainerConfig.port)
    }


}