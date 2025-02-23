package com.ssginc.unnie.board.dto.comment;

/**
 * 댓글 생성 요청 전용 DTO
 */
public class CommentCreateRequest {
    private String boardId; // 게시글 식별 번호
    private String commentContents; // 내용
    private String commentParentId; // 원댓글 번호
    private String commentMemberId; // 작성자
}
