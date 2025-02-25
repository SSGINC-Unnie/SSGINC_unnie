package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;

/**
 * Jwt 관련 예외 클래스.
 */
public class UnnieJwtException extends UnnieException {
    public UnnieJwtException(ErrorCode errorCode) {
        super(errorCode);
    }
    public UnnieJwtException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
