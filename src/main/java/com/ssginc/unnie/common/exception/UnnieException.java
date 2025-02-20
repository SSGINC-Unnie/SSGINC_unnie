package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;

/**
 * 애플리케이션의 공통 예외 처리 추상 클래스.
 * <p>
 * 에러 코드와 메시지를 관리하며 다양한 형태의 예외 생성자를 제공합니다.
 * 예외 처리 시 일관된 에러 코드 및 메시지를 사용할 수 있도록 설계되었습니다.
 */
public abstract class UnnieException extends RuntimeException {

    /**
     * 에러 코드 번호
     */

    protected UnnieException(ErrorCode errorCode) {
        super(errorCode.getMsg());
    }

    protected UnnieException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMsg(), cause);
    }

}
