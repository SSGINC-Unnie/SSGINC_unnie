package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;

/**
 * 파일 관련 커스텀 예외 클래스.
 */
public class UnnieMediaException extends UnnieException {
    public UnnieMediaException(ErrorCode errorCode) {super(errorCode);}
    public UnnieMediaException(ErrorCode errorCode, Throwable cause) {super(errorCode, cause);}
}
