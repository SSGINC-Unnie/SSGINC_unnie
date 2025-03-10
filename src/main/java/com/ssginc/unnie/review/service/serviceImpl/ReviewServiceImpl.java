package com.ssginc.unnie.review.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieReviewException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.review.dto.*;
import com.ssginc.unnie.review.mapper.ReviewMapper;
import com.ssginc.unnie.review.service.ReceiptService;
import com.ssginc.unnie.review.service.ReviewService;
import com.ssginc.unnie.common.util.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final ReceiptService receiptService;  // 영수증 인증 검증 서비스
    private final Validator<ReviewRequestBase> reviewValidator;  // ReviewValidator (ReviewRequestBase 타입)

    /**
     * 리뷰 저장: review 테이블과 review_keyword 테이블에 데이터를 등록합니다.
     * 트랜잭션 내에서 두 작업이 모두 성공해야 커밋됩니다.
     *
     * @param reviewCreateRequest 리뷰 등록 요청 DTO
     * @return 생성된 리뷰의 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createReview(ReviewCreateRequest reviewCreateRequest) {
        System.out.println("*******************************");
        System.out.println(reviewCreateRequest);
        System.out.println("*******************************");

        // 1. 영수증 인증 검증
        if (!receiptService.isReceiptVerified(reviewCreateRequest.getReviewReceiptId())) {
            throw new UnnieReviewException(ErrorCode.INVALID_RECEIPT);
        }

        // 2. 리뷰 요청 유효성 검증 (ReviewValidator 활용)
        if (!reviewValidator.validate(reviewCreateRequest)) {
            throw new UnnieReviewException(ErrorCode.INVALID_REVIEW_CONTENT);
        }

        // 3. 리뷰 등록 (review 테이블)
        int insertCount = reviewMapper.insertReview(reviewCreateRequest);
        if (insertCount == 0) {
            throw new UnnieReviewException(ErrorCode.DUPLICATE_REVIEW);
        }

        // 4. review_keyword 등록
        int keywordInsertCount = reviewMapper.insertReviewKeywordsForCreate(reviewCreateRequest);
        if (keywordInsertCount == 0) {
            throw new UnnieReviewException(ErrorCode.KEYWORDS_NOT_FOUND);
        }
        return reviewCreateRequest.getReviewId();
    }

    /**
     * 리뷰 조회: reviewId에 해당하는 리뷰 상세 정보를 반환합니다.
     *
     * @param reviewId 조회할 리뷰 ID
     * @return 조회된 리뷰 DTO
     */
    @Override
    public ReviewGetResponse getReviewById(long reviewId) {
        ReviewGetResponse review = reviewMapper.getReviewById(reviewId);
        if (review == null) {
            throw new UnnieReviewException(ErrorCode.REVIEW_NOT_FOUND);
        }
        return review;
    }

    /**
     * 키워드 조회: reviewId에 해당하는 리뷰의 키워드 목록을 반환합니다.
     *
     * @param reviewId 조회할 리뷰 ID
     * @return 키워드 문자열 목록
     */
    @Override
    public List<String> selectReviewKeywordsByReviewId(long reviewId) {
        List<String> keywords = reviewMapper.selectReviewKeywordsByReviewId(reviewId);
        if (keywords == null || keywords.isEmpty()) {
            throw new UnnieReviewException(ErrorCode.KEYWORDS_NOT_FOUND);
        }
        return keywords;
    }

    /**
     * 리뷰 수정: review 테이블 업데이트와 함께, 기존 키워드 정보를 삭제한 후 새로운 키워드 정보를 삽입합니다.
     * 키워드 수정 의사가 없으면 DTO의 keywordIds가 null이어야 하므로 기존 키워드를 그대로 유지합니다.
     * 모든 작업은 트랜잭션 내에서 실행됩니다.
     *
     * @param reviewUpdateRequest 리뷰 수정 요청 DTO
     * @return 수정된 리뷰의 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updateReview(ReviewUpdateRequest reviewUpdateRequest) {
        // 1. 소유자 확인: 토큰에서 전달받은 reviewMemberId와 리뷰 작성자 일치 여부 확인
        Integer ownership = reviewMapper.checkReviewAndAuthor(
                reviewUpdateRequest.getReviewId(),
                reviewUpdateRequest.getReviewMemberId()
        );
        if (ownership == null || ownership != 1) {
            throw new UnnieReviewException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        // 2. 리뷰 요청 유효성 검증 (ReviewValidator 활용)
        // update의 경우, 키워드가 null이면 기존 키워드를 유지하므로 null인 경우에는 검증하지 않음
        if (reviewUpdateRequest.getKeywordId() != null && !reviewValidator.validate(reviewUpdateRequest)) {
            throw new UnnieReviewException(ErrorCode.INVALID_REVIEW_CONTENT);
        }

        // 3. 리뷰 테이블 업데이트
        int updateCount = reviewMapper.updateReview(reviewUpdateRequest);
        if (updateCount == 0) {
            throw new UnnieReviewException(ErrorCode.REVIEW_UPDATE_FAILED);
        }

        // 4. 키워드 수정이 포함된 경우 처리
        if (reviewUpdateRequest.getKeywordId() != null) {
            reviewMapper.deleteReviewKeywords(reviewUpdateRequest.getReviewId());
            int keywordUpdateCount = reviewMapper.insertReviewKeywordsForUpdate(reviewUpdateRequest);
            if (keywordUpdateCount == 0) {
                throw new UnnieReviewException(ErrorCode.KEYWORDS_NOT_FOUND);
            }
        }
        return reviewUpdateRequest.getReviewId();
    }

    /**
     * 키워드 삭제: 리뷰 수정/삭제 시 review_keyword 테이블의 연결 정보를 삭제합니다.
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @return 삭제된 행의 수
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteReviewKeywords(long reviewId) {
        int deleteCount = reviewMapper.deleteReviewKeywords(reviewId);
        if (deleteCount == 0) {
            throw new UnnieReviewException(ErrorCode.KEYWORDS_NOT_FOUND);
        }
        return deleteCount;
    }

    /**
     * 리뷰 소프트 딜리트: 리뷰 존재 여부를 확인한 후, review_status를 전달받은 상태(1: 사용자 삭제, 2: 관리자 삭제)로 업데이트합니다.
     *
     * @param reviewId     삭제할 리뷰의 ID
     * @param reviewStatus 삭제 상태 (1 또는 2)
     * @return 삭제 처리된 리뷰의 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long softDeleteReview(long reviewId, int reviewStatus) {
        // 리뷰 존재 여부 확인
        boolean exists = reviewMapper.existsReview(reviewId);
        if (!exists) {
            log.error("리뷰가 존재하지 않거나 이미 삭제되었습니다. reviewId: {}", reviewId);
            throw new UnnieReviewException(ErrorCode.REVIEW_NOT_FOUND);
        }
        int deleteCount = reviewMapper.softDeleteReview(reviewId, reviewStatus);
        if (deleteCount == 0) {
            throw new UnnieReviewException(ErrorCode.REVIEW_DELETE_FAILED);
        }
        log.info("리뷰 소프트 딜리트 완료. reviewId: {}, status: {}", reviewId, reviewStatus);
        return reviewId;
    }

    /**
     * 업체 리뷰 목록 조회(회원 전용)
     *
     * @param shopId   업체 ID
     * @param keyword  필터링할 키워드 (없으면 null 또는 빈 문자열)
     * @param sortType 정렬 방식 ('newest', 'oldest')
     * @param offset   페이지네이션 시작 값
     * @param limit    조회 건수
     * @return
     */
    @Override
    public List<ReviewGetResponse> getReviewListByShop(long shopId, String keyword, String sortType, int offset, int limit) {
        // 입력값 검증
        if (shopId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 업체 ID입니다.");
        }
        // keyword가 null이면 빈 문자열로 설정 (쿼리에서 null 체크와 동일하게 동작하도록)
        if (keyword == null) {
            keyword = "";
        }
        // 정렬 방식이 null이거나 예상한 값이 아닐 경우 기본값 설정
        if (sortType == null || (!sortType.equals("newest") && !sortType.equals("oldest"))) {
            sortType = "newest";
        }
        // 페이지네이션 파라미터가 음수일 경우 기본값으로 조정 (예: 0, 10)
        if (offset < 0) {
            offset = 0;
        }
        if (limit <= 0) {
            limit = 10;
        }

        // 매퍼를 호출하여 리뷰 목록 조회
        List<ReviewGetResponse> reviewList = reviewMapper.getReviewListByShop(shopId, keyword, sortType, offset, limit);

        // 결과 후처리(필요한 경우)
        // 예: 리뷰 목록이 null이면 빈 리스트 반환
        if (reviewList == null) {
            reviewList = new ArrayList<>();
        }

        return reviewList;
    }

    /**
     * 업체 리뷰 목록 조회(비회원 전용)
     *
     * @param shopId   업체 ID
     * @param keyword  필터링할 키워드 (없으면 null 또는 빈 문자열)
     * @param sortType 정렬 방식 ('newest', 'oldest')
     * @param offset   페이지네이션 시작 값
     * @param limit    조회 건수
     * @return
     */
    @Override
    public List<ReviewGuestGetResponse> getReviewListByShopGuest(long shopId, String keyword, String sortType, int offset, int limit) {
        // 입력값 검증 및 기본값 설정 (위와 동일)
        if (shopId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 업체 ID입니다.");
        }
        if (keyword == null) {
            keyword = "";
        }
        if (sortType == null || (!sortType.equals("newest") && !sortType.equals("oldest"))) {
            sortType = "newest";
        }
        if (offset < 0) {
            offset = 0;
        }
        if (limit <= 0) {
            limit = 3;  // 비회원의 경우 최근 리뷰 3개만 보여줄 수 있도록 기본값 설정 가능
        }

        // 기존 로직 재활용: 로그인용 리뷰 조회 결과를 가져옴
        List<ReviewGetResponse> reviews = reviewMapper.getReviewListByShop(shopId, keyword, sortType, offset, limit);

        // 비회원 전용 DTO로 변환 (리뷰 내용은 블러 처리)
        List<ReviewGuestGetResponse> guestReviews = reviews.stream().map(review -> {
            // 블러 처리는 단순 예시로 전체 글자를 '*'로 대체하거나,
            // 실제로는 CSS나 JS로 블러 효과를 줄 수 있으므로, 여기서는 예시로 문자열 변환 처리합니다.
            String blurredContent = review.getReviewContent().replaceAll(".", "*");

            return ReviewGuestGetResponse.builder()
                    .reviewId(review.getReviewId())
                    .reviewMemberId(review.getReviewMemberId())
                    .reviewReceiptId(review.getReviewReceiptId())
                    .reviewImage(review.getReviewImage())
                    .reviewRate(review.getReviewRate())
                    .reviewContent(blurredContent)
                    .reviewDate(review.getReviewDate())
                    .memberNickName(review.getMemberNickName())
                    .shopName(review.getShopName())
                    .build();
        }).collect(Collectors.toList());

        return guestReviews;
    }

    @Override
    public int getReviewCountByShop(long shopId, String keyword) {
        if (shopId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 업체 ID입니다.");
        }
        // keyword가 null이면 빈 문자열로 처리하여 if 조건이 false가 되도록 함
        if (keyword == null) {
            keyword = "";
        }
        return reviewMapper.getReviewCountByShop(shopId, keyword);
    }
}
