package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;

public class UnnieBusinessVerifyException extends UnnieException {
    public UnnieBusinessVerifyException(ErrorCode errorCode) {
        super(errorCode);
    }
    public UnnieBusinessVerifyException(ErrorCode errorCode, Throwable cause) {super(errorCode, cause);}
}
