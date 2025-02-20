package com.ssginc.unnie.media.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieMediaException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.generator.FileNameGenerator;
import com.ssginc.unnie.common.util.validation.FileValidator;
import com.ssginc.unnie.media.mapper.MediaMapper;
import com.ssginc.unnie.media.service.MediaService;
import com.ssginc.unnie.media.vo.MediaTargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaMapper mediaMapper;
    private final FileValidator fileValidator;
    private final FileNameGenerator fileNameGenerator;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public String uploadFile(MultipartFile file, String targetType, long targetId) {

        MediaTargetType type;
        try {
            type = MediaTargetType.valueOf(targetType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnnieMediaException(ErrorCode.INVALID_FILE_TARGET_TYPE);
        }

        fileValidator.validate(file);

        String fileOriginalName = file.getOriginalFilename();

        log.info("fileName = {}", fileOriginalName);

        String newFileName = fileNameGenerator.generateFileName(fileOriginalName);

        log.info("newFileName = {}", newFileName);

        log.info("uploadPath = {}", uploadPath);

        String fileUrn = uploadPath + newFileName;

        // 저장 경로 설정
        File destination = new File(fileUrn);

        log.info("destination = {}", destination.getAbsolutePath());

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
