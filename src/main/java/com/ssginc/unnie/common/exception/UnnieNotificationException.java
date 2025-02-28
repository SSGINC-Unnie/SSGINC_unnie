package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;

/**
 * 알림 관련 커스텀 예외 클래스.
 */
public class UnnieNotificationException extends UnnieException {
    public UnnieNotificationException(ErrorCode errorCode) {super(errorCode);}
    public UnnieNotificationException(ErrorCode errorCode, Throwable cause) {super(errorCode, cause);}
}
