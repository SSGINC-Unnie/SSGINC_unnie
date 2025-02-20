package com.ssginc.unnie.common.util.validation;

/**
 * 검증 관련 작업에 대한 인터페이스
 * @param <T>
 */
public interface Validator<T> {

    boolean validate(T object);
}
