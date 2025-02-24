package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;

/**
 * 로그인 관련 예외 클래스.
 */
public class UnnieLoginException extends UnnieException {
    public UnnieLoginException(ErrorCode errorCode) {
        super(errorCode);
    }
    public UnnieLoginException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
