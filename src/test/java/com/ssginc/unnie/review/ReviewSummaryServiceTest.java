package com.ssginc.unnie.review;

import com.ssginc.unnie.review.mapper.ReviewMapper;
import com.ssginc.unnie.review.service.OpenAIService;
import com.ssginc.unnie.review.service.serviceImpl.ReviewServiceImpl;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class ReviewSummaryServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ReviewSummaryServiceTest.class);
    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private OpenAIService openAIService;

    @BeforeEach
    public void setUp() {
        // @ExtendWith(MockitoExtension.class)를 사용하면 openMocks 호출이 필요 없습니다.
    }

    @Test
    public void testSummarizeAndSave_NoReviews() {
        log.info("리뷰 요약 테스트 시작 NoReviews : " );
        long shopId = 1;
        // 리뷰가 없는 경우: 빈 리스트 반환
        Mockito.when(reviewMapper.getAllReviewsByShopId(shopId))
                .thenReturn(Collections.emptyList());

        String summary = reviewService.summarizeAndSave(shopId);
        // 리뷰가 없으면 기본 문구를 반환해야 함
        assertEquals("아직 작성된 리뷰가 없습니다.", summary);
        log.info("아직 작성된 리뷰가 없습니다. " + summary);
        // updateShopReviewSummary가 기본 문구로 업데이트되었는지 검증
        Mockito.verify(reviewMapper).updateShopReviewSummary(shopId, "아직 작성된 리뷰가 없습니다.");
    }

    @Test
    public void testSummarizeAndSave_WithReviews() {
        log.info("리뷰 요약 테스트 시작 WithReviews : ");
        long shopId = 1;
        // 리뷰가 존재하는 경우: 두 개의 리뷰를 반환
        List<String> reviews = Arrays.asList("첫 번째 리뷰", "두 번째 리뷰");
        Mockito.when(reviewMapper.getAllReviewsByShopId(shopId)).thenReturn(reviews);

        // 두 리뷰를 합치면 "첫 번째 리뷰. 두 번째 리뷰. " 가 되어야 함
        String combinedReviews = "첫 번째 리뷰. 두 번째 리뷰. ";
        // OpenAIService가 이 리뷰를 요약하여 결과를 반환한다고 가정
        Mockito.when(openAIService.summarizeReview(combinedReviews)).thenReturn("요약된 리뷰 내용");

        String summary = reviewService.summarizeAndSave(shopId);
        assertEquals("요약된 리뷰 내용", summary);
        log.info("요약된 리뷰 내용: " + summary);
        // shop 테이블 업데이트가 제대로 호출되었는지 검증
        Mockito.verify(reviewMapper).updateShopReviewSummary(shopId, "요약된 리뷰 내용");
    }
}
