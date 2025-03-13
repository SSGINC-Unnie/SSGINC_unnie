package com.ssginc.unnie.mypage.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieReviewException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.mypage.dto.review.MyPageReviewResponse;
import com.ssginc.unnie.mypage.mapper.MyPageReviewMapper;
import com.ssginc.unnie.mypage.service.MyPageReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 마이페이지 나의 리뷰 관련 인터페이스 구현 클래스
 */
@Service
@RequiredArgsConstructor
public class MyPageReviewServiceImpl implements MyPageReviewService {
    private final MyPageReviewMapper myPageReviewMapper;

    /**
     * 리뷰 목록 조회(마이페이지-내가 작성한 리뷰)
     * @param reviewMemberId 리뷰 작성자 ID
     * @param sortType 정렬 방식 ("newest" 또는 "oldest")
     * @param offset 조회 시작 인덱스
     * @param limit 조회 개수
     * @return 리뷰 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<MyPageReviewResponse> getReviewListById(long reviewMemberId, String sortType, int offset, int limit) {
        // 리뷰 작성자(reviewMemberId)에 해당하는 리뷰 목록을 조회합니다.
        List<MyPageReviewResponse> reviews = myPageReviewMapper.getReviewListById(reviewMemberId, sortType, offset, limit);
        //리뷰가 null이면 빈 리스트를 리턴
        return reviews != null ? reviews : Collections.emptyList();
    }

    @Override
    public int getReviewCountById(long reviewMemberId) {
        if (reviewMemberId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 회원입니다.");
        }
        return myPageReviewMapper.getReviewCountByMemberId(reviewMemberId);
    }
}
