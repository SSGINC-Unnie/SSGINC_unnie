package com.ssginc.unnie.media.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.media.service.MediaService;
import com.ssginc.unnie.media.vo.MediaTargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 파일 업로드 관련 컨트롤러 클래스
 */

@Slf4j
@RestController // @ResponseBody 생략 가능
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    /**
     * 파일을 서버에 업로드합니다.
     * @param file 업로드할 파일
     * @return http 응답 객체
     */
    @PostMapping("upload")
    public ResponseEntity<ResponseDto<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "targetType", required = false) String targetType,
            @RequestParam(value = "targetId", required = false) Long targetId) {

        log.info("파일 업로드 컨트롤러 실행");

        String urn = mediaService.uploadFile(file, targetType, targetId);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "파일 업로드에 성공했습니다.", Map.of("fileUrn", urn))
        );
    }

    @DeleteMapping("upload")
    public ResponseEntity<ResponseDto<Void>> deleteFile(@RequestParam("fileUrn") String fileUrn) {
        mediaService.deleteFile(fileUrn);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "파일 삭제에 성공했습니다.", null));
    }

    @GetMapping("file")
    public ResponseEntity<ResponseDto<Map<String, List<String>>>> getFileUrns(
            @RequestParam("targetType") String targetType,
            @RequestParam("targetId") long targetId) {
        log.info("파일 URN 조회, targetType: {}, targetId: {}", targetType, targetId);
        List<String> fileUrns = mediaService.getFileUrns(targetType, targetId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "파일 조회에 성공했습니다.", Map.of("fileUrns", fileUrns))
        );
    }
}
