package com.ssginc.unnie.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeCreatedEvent {
    private long receiverId;
    private String memberNickname;
    private long targetId;
    private String targetTitle;
    private String type;
}
