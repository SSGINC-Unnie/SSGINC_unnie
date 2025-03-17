package com.ssginc.unnie.review.debounce.service;

import java.util.Set;

/**
 * 리뷰 이벤트에 따른 디바운싱 처리를 위한 서비스 인터페이스.
 */
public interface ReviewDebounceService {

    /**
     * 리뷰 추가, 삭제 등 이벤트가 발생한 샵의 ID를 마킹합니다.
     *
     * @param shopId 업데이트 대상 샵 ID
     */
    void markShopForUpdate(long shopId);

    /**
     * 모아둔 샵 ID들을 가져온 후 컬렉션을 비웁니다.
     *
     * @return 업데이트가 필요한 샵 ID들의 집합
     */
    Set<Long> drainShops();
}