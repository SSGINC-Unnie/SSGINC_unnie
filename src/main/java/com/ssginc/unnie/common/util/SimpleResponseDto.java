package com.ssginc.unnie.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * http 응답 객체에 넣을 상태 코드와 메시지를 담은 dto 클래스입니다.
 */
@Data
@AllArgsConstructor
public class SimpleResponseDto {

    private int status; // HTTP 상태 코드
    private String message; // 응답 메시지
}
