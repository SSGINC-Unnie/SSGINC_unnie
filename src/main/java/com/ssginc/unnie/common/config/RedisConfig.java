package com.ssginc.unnie.common.config;

import com.ssginc.unnie.common.redis.RedisToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {



    //LettuceConnectionFactory: redis 서버와 연결 관리
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    //인증번호 저장용 RedisTemplate
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }

    //refresh token 저장용 RedisTemplate
    @Bean
    public RedisTemplate<String, RedisToken> redisTokenTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisToken> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // 키는 String으로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        // 값은 RedisToken 객체를 JSON 형식으로 직렬화
        Jackson2JsonRedisSerializer<RedisToken> serializer = new Jackson2JsonRedisSerializer<>(RedisToken.class);
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
}
