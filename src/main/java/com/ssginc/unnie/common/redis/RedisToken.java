package com.ssginc.unnie.common.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

/**
 * 회원의 refresh token 정보를 Redis에 저장하기 위한 객체
 * timeToLive 옵션을 통해 토큰의 TTL(Time To Live)을 21600초(6시간)로 설정
 */
@RedisHash(value = "RedisToken", timeToLive = 21600) //6시간
@Data
@AllArgsConstructor
public class RedisToken implements Serializable {

    @Id
    private String memberId;
    private String refreshToken;
}

