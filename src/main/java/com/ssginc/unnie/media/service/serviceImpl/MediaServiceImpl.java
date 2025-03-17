package com.ssginc.unnie.media.service.serviceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
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

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaMapper mediaMapper;
    private final Validator<MultipartFile> fileValidator;
    private final FileNameGenerator fileNameGenerator;
    private final AmazonS3 amazonS3; // S3 클라이언트 주입

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(MultipartFile file, String targetType, long targetId) {


        // 2) 파일 유효성 검증
        fileValidator.validate(file);

        // 3) 파일명 생성
        String fileOriginalName = file.getOriginalFilename();
        String newFileName = fileNameGenerator.generateFileName(fileOriginalName);

        // 4) S3 업로드
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            // 필요시 metadata.setContentType(file.getContentType()); 추가 가능
            amazonS3.putObject(new PutObjectRequest(bucketName, newFileName, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)); // 공개 접근 권한 설정
        } catch (IOException e) {
            throw new UnnieMediaException(ErrorCode.FILE_INTERNAL_SERVER_ERROR, e);
        }

        // 5) S3 URL 획득
        String fileUrn = amazonS3.getUrl(bucketName, newFileName).toString();

        // 6) DB insert (S3 URL 저장)
        MediaRequest mediaRequest = MediaRequest.builder()
                .targetType(targetType)
                .targetId(targetId)
                .fileUrn(fileUrn)
                .fileOriginalName(fileOriginalName)
                .newFileName(newFileName)
                .build();
        mediaMapper.insert(mediaRequest);

        // 7) 최종적으로 S3 URL 반환
        return fileUrn;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteFile(String fileUrn) {
        // S3 URL에서 파일 key 추출
        String key = fileUrn.substring(fileUrn.lastIndexOf("/") + 1);

        // 1) S3에서 파일 삭제
        amazonS3.deleteObject(bucketName, key);

        // 2) DB에서 해당 파일 정보 삭제
        int rows = mediaMapper.deleteByFileUrn(fileUrn);
        if (rows == 0) {
            throw new UnnieMediaException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> getFileUrns(String targetType, long targetId) {
        return mediaMapper.selectFileUrnByTarget(targetType.toUpperCase(), targetId);
    }
}
