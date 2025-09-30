package com.ssginc.unnie.main.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.main.service.serviceImpl.MainServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MainController {

    private final MainServiceImpl mainService;

    @Autowired
    public MainController(MainServiceImpl mainService) {
        this.mainService = mainService;
    }

    @GetMapping("/fetchVideos")
    public ResponseEntity<ResponseDto<Map<String, Object>>> fetchYouTubeVideos() {

        String youtubeResult = mainService.getYouTubeVideos();
        Map<String, Object> responseData = Map.of("youtubeResult", youtubeResult);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "파일 조회에 성공했습니다.", responseData));
    }
}