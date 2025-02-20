package com.ssginc.unnie.common.util.validation;

import com.ssginc.unnie.common.exception.UnnieException;
import com.ssginc.unnie.common.exception.UnnieShopException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.shop.dto.ShopInsertRequest;
import org.springframework.stereotype.Component;

@Component
public class ShopValidator implements Validator<ShopInsertRequest>{

    private final String nameReg;
    private final String phoneReg;
    private final String introReg;

    public ShopValidator() {
        nameReg = "^[가-힣]{2,16}$";
        phoneReg = "^(?:02-(?:\\d{3}-\\d{4}|\\d{4}-\\d{4})|0\\d{2}-\\d{3}-\\d{4})$";
        introReg = "^.{0,200}$";
    }


    @Override
    public boolean validate(ShopInsertRequest object) {
        if (object == null) {
            throw new UnnieShopException(ErrorCode.NULL_POINTER_ERROR);
        }

        validateName(object.getShopName());
        validateTel(object.getShopTel());
        validateIntro(object.getShopIntroduction());

            return true;
    }

    public void validateName(String name) {
        validateWithRegex(name, nameReg, ErrorCode.SHOP_INSERT_FAILED);
    }
    public void validateTel(String phone) {
        validateWithRegex(phone, phoneReg, ErrorCode.SHOP_INSERT_FAILED);
    }

    public void validateIntro(String intro) {
        validateWithRegex(intro, introReg, ErrorCode.SHOP_INSERT_FAILED);
    }

    private void validateWithRegex(String value, String regex, ErrorCode errorCode) {
        if (value == null || !value.matches(regex)) {
            throw new UnnieShopException(errorCode) {
            };
        }
    }


}
