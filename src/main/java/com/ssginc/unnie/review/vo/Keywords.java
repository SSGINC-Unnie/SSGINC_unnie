package com.ssginc.unnie.review.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 리뷰 키워드 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Keywords {
    private int keywordId; //키워드 번호
    private String keyword; //키워드
}
