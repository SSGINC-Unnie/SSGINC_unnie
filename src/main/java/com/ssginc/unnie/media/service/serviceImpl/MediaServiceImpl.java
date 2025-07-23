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
    public String uploadFile(MultipartFile file, String targetType, long targetId) {
        // 1) Enum 검증
        MediaTargetType type;
        try {
            type = MediaTargetType.valueOf(targetType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnnieMediaException(ErrorCode.INVALID_FILE_TARGET_TYPE);
        }

        // 2) 파일 유효성 검증
        fileValidator.validate(file);

        // 3) 파일명 생성
        String fileOriginalName = file.getOriginalFilename();
        String newFileName = fileNameGenerator.generateFileName(fileOriginalName);

        // 4) 실제 물리 경로 (uploadPath + 새 파일명)
        String physicalPath = uploadPath + newFileName;

        // 5) 파일 저장
        File destination = new File(physicalPath);
        log.info("fileName = {}, newFileName = {}, destination = {}",
                fileOriginalName, newFileName, destination.getAbsolutePath());

        try {
            if (!destination.exists()) {
                destination.mkdirs(); // 상위 디렉토리가 없으면 생성
            }
            file.transferTo(destination);

        } catch (IOException e) {
            throw new UnnieMediaException(ErrorCode.FILE_INTERNAL_SERVER_ERROR, e);
        }

        String fileUrn = "/upload/" + newFileName;

        // 6) DB insert (로컬 경로 저장)
        MediaRequest mediaRequest = MediaRequest.builder()
                .targetType(targetType)
                .targetId(targetId)
                .fileUrn(fileUrn)
                .fileOriginalName(fileOriginalName)
                .newFileName(newFileName)
                .build();
        mediaMapper.insert(mediaRequest);

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
