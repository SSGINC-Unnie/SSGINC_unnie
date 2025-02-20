package com.ssginc.unnie.media.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.media.service.MediaService;
import com.ssginc.unnie.media.vo.MediaTargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<ResponseDto<Map<String, String>>> uploadFile (@RequestParam("file") MultipartFile file,
                                                                        @RequestParam("targetType") String targetType,
                                                                        @RequestParam("targetId") String targetId) {
        log.info("파일 업로드 컨트롤러 실행");
        String urn = mediaService.uploadFile(file, targetType, Long.parseLong(targetId));
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "파일 업로드에 성공했습니다.", Map.of("fileUrn", urn))
        );
    }



}
