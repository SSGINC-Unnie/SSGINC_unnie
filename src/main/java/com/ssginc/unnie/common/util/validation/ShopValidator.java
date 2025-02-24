package com.ssginc.unnie.common.util.validation;

import com.ssginc.unnie.common.exception.UnnieShopException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.mypage.dto.DesignerCreateRequest;
import com.ssginc.unnie.mypage.dto.ProcedureCreateRequest;
import com.ssginc.unnie.mypage.dto.ShopCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class ShopValidator implements Validator<ShopCreateRequest> {

    private final String nameReg;
    private final String phoneReg;
    private final String introReg;

    public ShopValidator() {
        // 업체명: 한글 2~16자
        this.nameReg = "^[가-힣]{2,16}$";
        // 전화번호: (예시) 02-xxx-xxxx, 02-xxxx-xxxx, 0xx-xxx-xxxx 등
        this.phoneReg = "^(?:02-(?:\\d{3}-\\d{4}|\\d{4}-\\d{4})|0\\d{2}-\\d{3}-\\d{4})$";
        // 소개글: 최대 200자
        this.introReg = "^.{0,200}$";
    }

    /**
     * ======================= 업체 등록 ==============================
     */
    @Override
    public boolean validate(ShopCreateRequest object) {
        // 1) 객체 자체가 null인 경우
        if (object == null) {
            throw new UnnieShopException(ErrorCode.NULL_POINTER_ERROR);
        }

        // 2) 필수 입력값 누락 여부 검사 ()
        validateRequiredFields(object);
        // 3) 업체명 형식 검사
        validateName(object.getShopName());
        // 4) 전화번호 형식 검사
        validateTel(object.getShopTel());
        // 5) 소개글 형식 검사
        validateIntro(object.getShopIntroduction());


        return true;
    }

    /**
     * 필수 입력값 누락 여부 검사
     */
    private void validateRequiredFields(ShopCreateRequest request) {
        if (isEmpty(request.getShopName()) ||
                isEmpty(request.getShopLocation()) ||
                request.getShopCategory() == null ||
                request.getShopBusinessTime() == null ||
                isEmpty(request.getShopTel()) ||
                isEmpty(request.getShopIntroduction())) {

            throw new UnnieShopException(ErrorCode.SHOP_MISSING_REQUIRED_FIELD);
        }
    }

    /**
     * =============== 2) DesignerCreateRequest 검증 로직 ===============
     */
    public boolean validateDesigner(DesignerCreateRequest object) {
        if (object == null) {
            throw new UnnieShopException(ErrorCode.NULL_POINTER_ERROR);
        }

        // 디자이너 필수 입력값 검사
        validateRequiredFieldsForDesigner(object);

        // 디자이너 이름 형식 검사
        validateWithRegex(object.getDesignerName(), nameReg, ErrorCode.INVALID_NAME_FORMAT);

        // 디자이너 소개글 형식 검사
        validateWithRegex(object.getDesignerIntroduction(), introReg, ErrorCode.INVALID_INTRODUCTION_FORMAT);

        return true;
    }

    public void validateShopReference(boolean exists) {
        if (!exists) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
    }

    private void validateRequiredFieldsForDesigner(DesignerCreateRequest request) {
        if (isEmpty(request.getDesignerName()) ||
                isEmpty(request.getDesignerIntroduction())) {

            throw new UnnieShopException(ErrorCode.SHOP_MISSING_REQUIRED_FIELD);
        }
    }

    /**
     * =============== 3) ProcedureCreateRequest 검증 로직 ===============
     */

    public boolean validateProcedure(ProcedureCreateRequest object) {
        if (object == null) {
            throw new UnnieShopException(ErrorCode.NULL_POINTER_ERROR);
        }

        // 필수 입력값 누락 여부 검사
        validateRequiredFieldsForProcedure(object);

        validateWithRegex(object.getProcedureName(), nameReg, ErrorCode.INVALID_NAME_FORMAT);

        // 프로시저 가격이 양수인지 검사
        if (object.getProcedurePrice() <= 0) {
            throw new UnnieShopException(ErrorCode.INVALID_PROCEDURE_PRICE);
        }

        return true;
    }

    private void validateRequiredFieldsForProcedure(ProcedureCreateRequest request) {
        if (request.getProcedureDesignerId() <= 0 || isEmpty(request.getProcedureName())) {
            throw new UnnieShopException(ErrorCode.SHOP_MISSING_REQUIRED_FIELD);
        }
    }

    public void validateDesignerReference(boolean exists) {
        if (!exists) {
            throw new UnnieShopException(ErrorCode.DESIGNER_NOT_FOUND);
        }
    }





    /**
     * ================= 공통 검증 메서드 ==========================
     */

    public void validateName(String name) {
        validateWithRegex(name, nameReg, ErrorCode.INVALID_NAME_FORMAT);
    }


    public void validateTel(String phone) {
        validateWithRegex(phone, phoneReg, ErrorCode.INVALID_TEL_FORMAT);
    }

    public void validateIntro(String intro) {
        validateWithRegex(intro, introReg, ErrorCode.INVALID_INTRODUCTION_FORMAT);
    }

    /**
     * 이미 존재하는 업체인지 여부 검사
     * - 실제로는 DB 조회 결과 등을 파라미터로 받아 처리
     */
    public void validateDuplicateShopName(boolean isDuplicate) {
        if (isDuplicate) {
            throw new UnnieShopException(ErrorCode.SHOP_ALREADY_EXISTS);
        }
    }

    /**
     * 이미 사용 중인 전화번호인지 여부 검사
     * - 실제로는 DB 조회 결과 등을 파라미터로 받아 처리
     */
    public void validateDuplicatePhoneNumber(boolean isDuplicate) {
        if (isDuplicate) {
            throw new UnnieShopException(ErrorCode.DUPLICATE_SHOP_TEL);
        }
    }


    /**
     * 문자열이 null이거나 비어있는지 검사하는 유틸 메서드
     */
    private boolean isEmpty(String value) {
        return (value == null || value.trim().isEmpty());
    }

    /**
     * 정규식 기반 검증 공통 메서드
     */
    private void validateWithRegex(String value, String regex, ErrorCode errorCode) {
        if (value == null || !value.matches(regex)) {
            throw new UnnieShopException(errorCode);
        }
    }
}
