package com.ssginc.unnie.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * refresh token의 저장, 조회, 삭제 기능 인터페이스
 */
public interface RedisTokenService {

    //refresh token 저장
    void saveRefreshToken(String memberId, String refreshToken);

    //refresh token 조회
    RedisToken getRefreshToken(String memberId);

    //refresh token 삭제
    void deleteRefreshToken(String memberId);
}