package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;

/**
 * 회원가입 관련 예외 클래스.
 */
public class UnnieRegisterException extends UnnieException {
    public UnnieRegisterException(ErrorCode errorCode) {
        super(errorCode);
    }
    public UnnieRegisterException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
