package com.ssginc.unnie.like.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.like.dto.LikeRequest;
import com.ssginc.unnie.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 좋아요 기능 관련 컨트롤러 클래스
 */
@Slf4j
@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /**
     * 좋아요 여부 확인 컨트롤러 메서드
     */
    @PostMapping("/status")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getLikeStatus (LikeRequest like) {

        long memberId = 1;

        like.setLikeMemberId(memberId);

        log.info("like = {}", like);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "좋아요 여부 확인", Map.of("liked", likeService.getLikeStatus(like)))
        );
    }
}
