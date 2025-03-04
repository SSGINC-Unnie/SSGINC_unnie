package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;

/**
 * 회원관리 관련 예외 클래스.
 */
public class UnnieMemberException extends UnnieException {
    public UnnieMemberException(ErrorCode errorCode) {
        super(errorCode);
    }
    public UnnieMemberException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
