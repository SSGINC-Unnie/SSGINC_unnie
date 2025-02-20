package com.ssginc.unnie.common.exception;

import com.ssginc.unnie.common.util.ErrorCode;

public class UnnieShopException extends UnnieException{
    public UnnieShopException(ErrorCode errorcode) {super(errorcode);}
    public UnnieShopException(ErrorCode errorCode, Throwable cause) {super(errorCode, cause);}

}
