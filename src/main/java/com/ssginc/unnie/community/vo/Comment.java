package com.ssginc.unnie.community.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 댓글 VO 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private long commentId; // 댓글 식별 번호
    private String commentContents; // 댓글 내용
    private LocalDateTime commentCreatedAt; // 댓글 생성일
    private LocalDateTime commentUpdatedAt; // 댓글 수정일
    private boolean commentIsActive; // 댓글 삭제 여부
    private long commentBoardId; // 댓글 달린 게시글 식별 번호
    private long commentParentId; // (대댓글일 시)원댓글 식별 번호
    private long commentMemberId; // 댓글 작성자 식별 번호
}
