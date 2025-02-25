package com.ssginc.unnie.like.vo;

/**
 * 좋아요 VO 클래스
 */
public class Like {
    private long likeId; // 좋아요 식별 번호
    private LikeTargetType likeTargetType; // 좋아요 타입
    private long likeTargetId; // 좋아요 대상 식별 번호
    private long likeMemberId; // 좋아요 누른 유저 식별 번호
}
