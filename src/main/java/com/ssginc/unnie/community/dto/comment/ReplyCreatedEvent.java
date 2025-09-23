package com.ssginc.unnie.community.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class ReplyCreatedEvent {
    private final long receiverId; // 부모 댓글 작성자 ID
    private final long commentMemberId;
    private final String commentMemberNickname;
    private final long commentBoardId;
    private final long parentCommentId;
}
