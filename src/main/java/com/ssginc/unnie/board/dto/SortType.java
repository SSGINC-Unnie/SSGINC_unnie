package com.ssginc.unnie.board.dto;

import java.util.Arrays;

public enum SortType {
    LATEST, POPULAR;

    // 유효성 검증 메서드 추가
    public static boolean isValid(String value) {
        return Arrays.stream(SortType.values())
                .map(Enum::name)
                .anyMatch(v -> v.equalsIgnoreCase(value));
    }
}
