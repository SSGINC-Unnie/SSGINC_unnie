package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;

public class UnnieReservationException extends UnnieException {
    public UnnieReservationException(ErrorCode errorCode) {
        super(errorCode);
    }
    public UnnieReservationException(ErrorCode errorCode, Throwable cause) {super(errorCode, cause);}
}
