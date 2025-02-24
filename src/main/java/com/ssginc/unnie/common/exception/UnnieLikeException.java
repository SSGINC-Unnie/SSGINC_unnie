package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
    좋아요 기능 관련 예외 클래스.
 */
@Getter
@Slf4j
public class UnnieLikeException extends UnnieException {
    public UnnieLikeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnnieLikeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
