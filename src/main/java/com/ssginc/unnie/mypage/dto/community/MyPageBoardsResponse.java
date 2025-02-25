package com.ssginc.unnie.mypage.dto.community;

import lombok.*;

/**
 * 나의 게시글 조회 전용 DTO
 */
@Data
public class MyPageBoardsResponse {
    private long boardId; // 게시글 식별 번호
    private String boardTitle; // 게시글 제목
    private String boardThumbnail;// 대표 이미지
    private String boardCreatedAt; // 작성일
    private long boardViews; // 조회 수
    private int likeCount; // 좋아요 수
    private int commentCount; // 댓글 수
}
