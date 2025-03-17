package com.ssginc.unnie.review.debounce.service.serviceImpl;

import com.ssginc.unnie.review.debounce.service.ReviewDebounceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * DebounceService 인터페이스의 기본 구현체.
 * 이벤트로 마킹된 shopId를 인메모리에서 관리합니다.
 * 리뷰가 업데이트 된 shopId를 모아 일정시간 지난 후 api 호출하여 리뷰 요약 업데이트
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewDebounceServiceImpl implements ReviewDebounceService {
    // Redis 내에 업데이트 대상 shopId를 저장할 키 (Set 타입)
    private static final String SHOP_IDS_KEY = "pending:shop:updates";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void markShopForUpdate(long shopId) {
        // Redis Set에 shopId 추가 (중복 방지를 위해 String으로 저장)
        redisTemplate.opsForSet().add(SHOP_IDS_KEY, String.valueOf(shopId));
    }

    @Override
    public Set<Long> drainShops() {
        // 저장된 shopId들을 모두 가져옵니다.
        Set<String> shopIdStrings = redisTemplate.opsForSet().members(SHOP_IDS_KEY);
        // 키 삭제하여 해당 데이터를 drain(비우기) 처리
        redisTemplate.delete(SHOP_IDS_KEY);
        // String Set을 Long Set으로 변환하여 반환
        return shopIdStrings.stream().map(Long::valueOf).collect(Collectors.toSet());
    }
}
