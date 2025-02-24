package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
    게시글 관련 예외 클래스.
 */
@Getter
@Slf4j
public class UnnieReviewException extends UnnieException {
    public UnnieReviewException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnnieReviewException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
