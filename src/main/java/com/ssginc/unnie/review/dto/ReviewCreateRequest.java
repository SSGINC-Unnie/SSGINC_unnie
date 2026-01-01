package com.ssginc.unnie.review.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 리뷰 DB insert 전용 dto 클래스
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ReviewCreateRequest extends ReviewRequestBase{

    private MultipartFile file;

    public ReviewCreateRequest(long reviewId, // 리뷰 번호
                               long reviewMemberId, // 작성자 번호
                               Long reviewReceiptId,
                               Long reviewReservationId,
                               String reviewImage, // 리뷰 대표 이미지
                               int reviewRate, // 리뷰 별점
                               String reviewContent, // 리뷰 내용
                               List<Integer> keywordId,// 선택한 키워드들의 ID 목록
                               MultipartFile file)
    {
        super(reviewId, reviewMemberId, reviewReceiptId, reviewReservationId, reviewImage, reviewRate, reviewContent, keywordId, file);        this.file = file;
    }
}
