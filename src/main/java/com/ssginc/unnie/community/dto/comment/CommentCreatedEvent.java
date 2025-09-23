package com.ssginc.unnie.community.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class CommentCreatedEvent {
    private final long commentBoardId;
    private final long boardAuthorId;
    private final long commentMemberId;
    private final String commentMemberNickname;
    private final String boardTitle;
    private final String commentContent;

}
