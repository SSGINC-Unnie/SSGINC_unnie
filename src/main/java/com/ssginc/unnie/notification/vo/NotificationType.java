package com.ssginc.unnie.notification.vo;

import com.ssginc.unnie.common.util.EnumDescription;
import lombok.Getter;

@Getter
public enum NotificationType implements EnumDescription {

    LIKE("좋아요"),
    COMMENT("댓글"),
    REPLY("대댓글"),
    REVIEW("리뷰"),
    WARN("경고"),
    PERMISSION("등록 승인"),
    DECLINE("등록 거절");


    private final String description;

    NotificationType(String description) {
        this.description = description;
    }
}
