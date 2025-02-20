package com.ssginc.unnie.common.util.generator;

import com.ssginc.unnie.common.exception.UnnieMediaException;
import com.ssginc.unnie.common.util.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 새로운 파일명 생성해주는 클래스
 */
@Component
@Slf4j
public class FileNameGenerator {

    public static String generateFileName(String originName){

        String extension = "";

        int lastDotIndex = originName.lastIndexOf(".");

        if (lastDotIndex == -1 || lastDotIndex == originName.length() - 1) {
            throw new UnnieMediaException(ErrorCode.INVALID_FILE_TYPE);
        }

        extension = originName.substring(lastDotIndex); // 확장자 포함

        return UUID.randomUUID() + extension;
    }
}
