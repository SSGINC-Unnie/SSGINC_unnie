package com.ssginc.unnie.exception;

/**
 * unnie 프로젝트의 최상위 커스텀 예외 클래스입니다.
 *
 * 추상클래스로 설계하며 이하 모든 커스텀 예외들이 본 클래스를 상속받도록 하였습니다.
 */
public abstract class UnnieCustomException extends RuntimeException {
    protected UnnieCustomException(String message) {
        super(message);
    }
}
