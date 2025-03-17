package com.ssginc.unnie.review.debounce;

import com.ssginc.unnie.review.debounce.service.ReviewDebounceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewEventListener {

    private final ReviewDebounceService debounceService;

    @Async
    @EventListener
    public void handleReviewCreatedEvent(ReviewCreatedEvent event) {
        // 리뷰가 추가된 샵의 ID를 표시해 둡니다.
        log.info("리뷰가 추가된 샵의 Id: {}", event);
        debounceService.markShopForUpdate(event.getShopId());
    }
}

