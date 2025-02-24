package com.ssginc.unnie.community.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 댓글 요청 전용 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequest {
    private long commentId; // 댓글 식별 번호
    private long commentBoardId; // 게시글 식별 번호
    private String commentContents; // 내용
    private long commentParentId; // 원댓글 번호
    private long commentMemberId; // 작성자
}
