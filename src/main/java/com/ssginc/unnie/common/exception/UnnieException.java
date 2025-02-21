package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 애플리케이션의 공통 예외 처리 추상 클래스.
 * <p>
 * 에러 코드와 메시지를 관리하며 다양한 형태의 예외 생성자를 제공합니다.
 * 예외 처리 시 일관된 에러 코드 및 메시지를 사용할 수 있도록 설계되었습니다.
 */
@Slf4j
@Getter
public abstract class UnnieException extends RuntimeException {

    /**
     * 에러 코드 번호
     */
    private final int status;
    private final String code;

    protected UnnieException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    protected UnnieException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMsg(), cause);
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();  // HTTP 상태 코드
        log.error(errorCode.getStatus() + " " + errorCode.getCode() + " " + errorCode.getMsg(), cause);
    }
}
