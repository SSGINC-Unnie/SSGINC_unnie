package com.ssginc.unnie.common.util.validation;

import com.ssginc.unnie.common.exception.UnnieMediaException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.parser.BoardParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 파일 업로드 검증 클래스
 */
@Slf4j
@Component
public class FileValidator implements Validator<MultipartFile> {

    // 허용할 파일 확장자 목록
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");

    // 허용할 MIME 타입 목록
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "text/plain"
    );

    // 최대 파일 크기 (30MB)
    private static final long MAX_FILE_SIZE = 30 * 1024 * 1024;

    // Apache Tika 인스턴스 (파일 내용 기반 MIME 검증)
    private static final Tika tika = new Tika();

    @Override
    public boolean validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new UnnieMediaException(ErrorCode.FILE_NOT_FOUND);
        }

        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new UnnieMediaException(ErrorCode.FILE_TOO_LARGE);
        }

        // 파일 확장자 검증
        String fileName = file.getOriginalFilename();
        if (fileName == null || !isValidExtension(fileName)) {
            throw new UnnieMediaException(ErrorCode.INVALID_FILE_TYPE);
        }

        String detectedMimeType;

        try {
            detectedMimeType = tika.detect(file.getInputStream());
            if (!ALLOWED_MIME_TYPES.contains(detectedMimeType)) {
                System.out.println(detectedMimeType);
                throw new UnnieMediaException(ErrorCode.INVALID_FILE_TYPE);
            }
        } catch (IOException e) {
            throw new UnnieMediaException(ErrorCode.FILE_INTERNAL_SERVER_ERROR, e);
        }


        log.info("파일 검증 성공: {}, MIME 타입: {}", fileName, detectedMimeType);
        return true;
    }

    /**
     * 파일 확장자 검증
     */
    private boolean isValidExtension(String fileName) {

        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            throw new UnnieMediaException(ErrorCode.INVALID_FILE_TYPE);
        }

        String extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }
}
