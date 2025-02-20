package com.ssginc.unnie.media;

import com.ssginc.unnie.common.util.generator.FileNameGenerator;
import com.ssginc.unnie.common.util.validation.FileValidator;
import com.ssginc.unnie.media.mapper.MediaMapper;
import com.ssginc.unnie.media.service.serviceImpl.MediaServiceImpl;
import com.ssginc.unnie.media.vo.MediaTargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MediaServiceImplTest {

    @Autowired
    private MediaServiceImpl mediaService; // ✅ 실제 Bean을 주입하여 테스트

    @Autowired
    private MediaMapper mediaMapper; // ✅ 실제 MyBatis Mapper 주입

    @Autowired
    private FileValidator fileValidator; // ✅ 실제 유효성 검사기 사용

    @Autowired
    private FileNameGenerator fileNameGenerator; // ✅ 실제 파일명 생성기 사용

    private MultipartFile mockFile;
    private static final String uploadPath = "src/main/resources/static/upload/";
    private static final String TARGET_TYPE = MediaTargetType.board.toString();
    private static final long TARGET_ID = 123L;

    @BeforeEach
    void setUp() {
        // ✅ 실제 이미지 데이터를 포함한 MockMultipartFile 생성
        mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    @Test
    void testUpload() {
        // ✅ 실제 파일명을 생성하여 테스트
        String newFileName = fileNameGenerator.generateFileName(mockFile.getOriginalFilename());
        String expectedUrn = uploadPath + newFileName;

        // ✅ When : 파일 업로드 실행
        String fileUrn = mediaService.uploadFile(mockFile, TARGET_TYPE, TARGET_ID);

        // ✅ Then : 결과 검증
        assertEquals(expectedUrn, fileUrn, "URN이 예상값과 일치해야 합니다.");

        // ✅ 파일 검증 메서드 호출 확인
        fileValidator.validate(mockFile);

        // ✅ 파일 저장 경로가 DB에 저장됐는지 확인
        mediaMapper.insertMedia(TARGET_TYPE, TARGET_ID, expectedUrn, mockFile.getOriginalFilename(), newFileName);

        // ✅ 파일이 실제로 저장됐는지 확인
        File savedFile = new File(expectedUrn);
        assertTrue(savedFile.exists(), "파일이 실제로 저장되어야 합니다.");

        // ✅ 테스트 종료 후 파일 삭제
        savedFile.delete();
    }
}