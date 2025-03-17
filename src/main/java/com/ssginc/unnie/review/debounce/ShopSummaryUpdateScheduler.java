package com.ssginc.unnie.review.debounce;

import com.ssginc.unnie.review.debounce.service.ReviewDebounceService;
import com.ssginc.unnie.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopSummaryUpdateScheduler {

    private final ReviewDebounceService debounceService;

    private final ReviewService reviewService; // summarizeAndSave 메서드 포함

    /**
     * 스케줄러: 6시간마다 Redis에 저장된 샵 ID들을 모아서 업데이트 진행
     * cron: 매일 0시, 6시, 12시, 18시에 실행
     */
    @Scheduled(cron = "0 0 */6 * * *")
    public void updateDebouncedShopSummaries() {
        Set<Long> shopIds = debounceService.drainShops();
        for (Long shopId : shopIds) {
            try {
                String summary = reviewService.summarizeAndSave(shopId);
                System.out.println("Shop " + shopId + " 리뷰 요약 업데이트 성공: " + summary);
            } catch (Exception e) {
                System.err.println("Shop " + shopId + " 리뷰 요약 업데이트 실패: " + e.getMessage());
            }
        }
    }
}
