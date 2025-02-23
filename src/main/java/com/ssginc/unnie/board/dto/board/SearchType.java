package com.ssginc.unnie.board.dto.board;

import java.util.Arrays;

public enum SearchType {
    TITLE, CONTENT;

    // 유효성 검증 메서드 추가
    public static boolean isValid(String value) {
        return Arrays.stream(SearchType.values())
                .map(Enum::name)
                .anyMatch(v -> v.equalsIgnoreCase(value));
    }
}
