package com.ssginc.unnie.community.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyCreatedEvent {
    private long receiverId; // 게시글 작성자(수신인)
    private String commentMemberNickname; // 댓글 작성자 닉네임
    private String commentContent; // 게시글 제목
    private long commentBoardId;
}
