package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;

/**
 * Id/pw 찾기 관련 예외 클래스.
 */
public class UnnieMemberFindException extends UnnieException {
    public UnnieMemberFindException(ErrorCode errorCode) {
        super(errorCode);
    }
    public UnnieMemberFindException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
