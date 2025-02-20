package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;

public class UnnieGeocodingApiException extends UnnieException {
    public UnnieGeocodingApiException(ErrorCode errorCode) {
        super(errorCode);
    }
    public UnnieGeocodingApiException(ErrorCode errorCode, Throwable cause) {super(errorCode, cause);}
}
