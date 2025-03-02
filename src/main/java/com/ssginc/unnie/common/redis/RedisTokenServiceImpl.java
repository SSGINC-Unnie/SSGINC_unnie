package com.ssginc.unnie.common.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * refresh token의 저장, 조회, 삭제 기능 인터페이스의 구현 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTokenServiceImpl implements RedisTokenService {

    private final RedisTokenRepository redisTokenRepository;

    //refresh token 저장
    @Override
    public void saveRefreshToken(String memberId, String refreshToken) {
        // RedisToken 객체를 생성하여 Redis에 저장
        RedisToken token = new RedisToken(memberId, refreshToken);
        redisTokenRepository.save(token);
    }

    //refresh token 조회
    @Override
    public RedisToken getRefreshToken(String memberId) {
        // memberId를 key로 사용하여 Redis에서 해당 토큰을 조회
        return redisTokenRepository.findById(memberId).orElse(null);
    }

    //refresh token 삭제
    @Override
    public void deleteRefreshToken(String memberId) {
        // memberId를 key로 사용하여 Redis에서 해당 토큰을 삭제
        redisTokenRepository.deleteById(memberId);
    }
}
