package com.ssginc.unnie.media.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieMediaException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.generator.FileNameGenerator;
import com.ssginc.unnie.common.util.validation.Validator;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaMapper mediaMapper;

    private final Validator<MultipartFile> fileValidator; // 유효성 검증 인터페이스

    private final FileNameGenerator fileNameGenerator;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(MultipartFile file, String targetType, long targetId) {

        MediaTargetType type;
        try {
            type = MediaTargetType.valueOf(targetType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnnieMediaException(ErrorCode.INVALID_FILE_TARGET_TYPE);
        }

        fileValidator.validate(file);

        String fileOriginalName = file.getOriginalFilename();

        String newFileName = fileNameGenerator.generateFileName(fileOriginalName);

        String fileUrn = uploadPath + newFileName;

        // 저장 경로 설정
        File destination = new File(fileUrn);

        log.info("fileName = {} / newFileName = {} / destination = {}", fileOriginalName, newFileName, destination.getAbsolutePath());

        try {
            if(!destination.exists()) {
                destination.mkdirs();
            }
            // 파일 저장
            file.transferTo(destination);

        } catch (IOException e) {
            throw new UnnieMediaException(ErrorCode.FILE_INTERNAL_SERVER_ERROR, e);
        }

        mediaMapper.insert(targetType, targetId, fileUrn, fileOriginalName, newFileName);

        return fileUrn;
    }
}
