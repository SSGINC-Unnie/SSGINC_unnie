package com.ssginc.unnie.board.vo;

import com.ssginc.unnie.common.util.EnumDescription;
import lombok.Getter;

@Getter
public enum BoardCategory implements EnumDescription {
    NOTICE("공지 있어!"),
    SALE("여기 할인어때?"),
    WHAT_SUITS_ME("나 뭐가 어울려?"),
    HEARD_THIS_TIP("이 꿀팁 들어봤어?"),
    HOWS_THIS_ITEM("이템 어때?"),
    FREE_BOARD("자유게시판");

    private final String description;

    BoardCategory(String description) {
        this.description = description;
    }

}

