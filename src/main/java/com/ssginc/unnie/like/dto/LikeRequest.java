package com.ssginc.unnie.like.dto;

import com.ssginc.unnie.like.vo.LikeTargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 좋아요 요청 DTO 클래스
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeRequest {
    private long likeId; // 좋아요 식별 번호
    private String likeTargetType; // 좋아요 타입
    private long likeTargetId; // 좋아요 대상 식별 번호
    private long likeMemberId; // 좋아요 누른 유저 식별 번호
}
