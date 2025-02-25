package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
    신고 관련 예외 클래스.
 */
@Getter
@Slf4j
public class UnnieReportException extends UnnieException {
    public UnnieReportException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnnieReportException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
