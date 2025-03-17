package com.ssginc.unnie.community.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreatedEvent {
    private long commentBoardId; // 게시글 식별 번호
    private long boardAuthorId; // 게시글 작성자(수신인)
    private String commentMemberNickname; // 댓글 작성자 닉네임
    private String boardTitle; // 게시글 제목
}
