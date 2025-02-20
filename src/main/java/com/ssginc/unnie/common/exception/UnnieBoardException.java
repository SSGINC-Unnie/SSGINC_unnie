package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
    게시글 관련 예외 클래스.
 */
@Getter
@Slf4j
public class UnnieBoardException extends UnnieException {
    public UnnieBoardException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnnieBoardException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
