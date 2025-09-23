package com.ssginc.unnie.mypage.dto.community;

import lombok.Data;

/**
 * 나의 댓글 조회 전용 DTO
 */
@Data
public class MyPageCommentsResponse {
    private long commentId; // 댓글 식별 번호
    private String commentCreatedAt; // 댓글 작성일
    private String commentContents; // 댓글 내용
    private String likeCount; // 좋아요 수
    private long boardId;           // 원본 게시글 ID
    private String boardTitle;      // 원본 게시글 제목
    private String boardThumbnail; // 원본 게시글 썸네일
    private int boardCommentCount; // 원본 게시글의 댓글 수


}
