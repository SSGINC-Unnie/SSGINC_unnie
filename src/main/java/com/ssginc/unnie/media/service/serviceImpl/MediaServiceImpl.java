package com.ssginc.unnie.media.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieMediaException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.generator.FileNameGenerator;
import com.ssginc.unnie.common.util.validation.Validator;
import com.ssginc.unnie.media.dto.MediaRequest;
import com.ssginc.unnie.media.mapper.MediaMapper;
import com.ssginc.unnie.media.service.MediaService;
import com.ssginc.unnie.media.vo.MediaTargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaMapper mediaMapper;
    private final Validator<MultipartFile> fileValidator;
    private final FileNameGenerator fileNameGenerator;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(MultipartFile file, String targetType, Long targetId) {
        // 1. 파일 유효성 검증
        fileValidator.validate(file);

        // 2. 고유한 파일명 생성
        String fileOriginalName = file.getOriginalFilename();
        String newFileName = fileNameGenerator.generateFileName(fileOriginalName);

        // 3. 파일을 저장할 절대 경로 생성 (Path 객체 사용)
        Path destinationFile = Paths.get(uploadPath, newFileName).toAbsolutePath();
        log.info("파일 저장 경로: {}", destinationFile);

        try {
            // 4. 파일이 저장될 부모 디렉토리를 가져옴
            Path parentDir = destinationFile.getParent();

            // 5. 부모 디렉토리가 존재하지 않으면 생성 (핵심 수정 사항!)
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            // 6. 파일 저장
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            log.error("파일 저장 중 심각한 오류 발생", e);
            throw new UnnieMediaException(ErrorCode.FILE_INTERNAL_SERVER_ERROR, e);
        }

        // 7. 웹 접근 경로(URN) 생성
        String fileUrn = "/upload/" + newFileName;

        // 8. targetId가 있을 경우에만 DB에 미디어 정보 저장
        if (targetId != null && targetType != null) {
            try {
                MediaTargetType.valueOf(targetType.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new UnnieMediaException(ErrorCode.INVALID_FILE_TARGET_TYPE);
            }

            MediaRequest mediaRequest = MediaRequest.builder()
                    .targetType(targetType)
                    .targetId(targetId)
                    .fileUrn(fileUrn)
                    .fileOriginalName(fileOriginalName)
                    .newFileName(newFileName)
                    .build();
            mediaMapper.insert(mediaRequest);
        }

        // 9. 웹 접근 경로 반환
        return fileUrn;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteFile(String fileUrn) {
        // fileUrn이 "/upload/..." 형식이므로, 접두어 제거하여 새 파일명 추출
        String prefix = "/upload/";
        if (!fileUrn.startsWith(prefix)) {
            throw new UnnieMediaException(ErrorCode.INVALID_FILE_TARGET_TYPE);
        }
        String newFileName = fileUrn.substring(prefix.length());
        String physicalPath = uploadPath + newFileName;

        // 물리 파일 삭제
        File file = new File(physicalPath);
        if (file.exists() && !file.delete()) {
            log.warn("물리 파일 삭제 실패: " + physicalPath);
        }

        int rows = mediaMapper.deleteByFileUrn(fileUrn);
        if (rows == 0) {
            throw new UnnieMediaException(ErrorCode.FILE_NOT_FOUND); // FILE_NOT_FOUND 코드가 존재해야 함
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> getFileUrns(String targetType, long targetId) {
        List<String> fileUrnList = mediaMapper.selectFileUrnByTarget(targetType.toUpperCase(), targetId);
        return fileUrnList;
    }
}
