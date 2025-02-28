package com.ssginc.unnie.like.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.like.dto.LikeRequest;
import com.ssginc.unnie.like.service.LikeService;
import com.ssginc.unnie.member.vo.Member;
import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.dto.NotificationResponse;
import com.ssginc.unnie.notification.service.ProducerService;
import com.ssginc.unnie.notification.vo.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final ProducerService producerService;

    /**
     * 좋아요 여부 확인 컨트롤러 메서드
     */
    @PostMapping("/status")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getLikeStatus (LikeRequest like,
                                                                           @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        like.setLikeMemberId(memberId);
        log.info("like = {}", like);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "좋아요 여부 확인", Map.of("liked", likeService.getLikeStatus(like)))
        );
    }

    /**
     * 좋아요 추가
     */
    @PostMapping("")
    public ResponseEntity<ResponseDto<Map<String, Object>>> createLike(LikeRequest like,
                                                                       @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();

        like.setLikeMemberId(memberId);

        NotificationResponse response = likeService.getLikeTargetMemberInfoByTargetInfo(like);

        NotificationMessage msg = likeService.createNotificationMsg(like, response);

        producerService.createNotification(msg);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "좋아요가 추가되었습니다.", Map.of("like", likeService.createLike(like)))
        );
    }

    /**
     * 좋아요 삭제(hard delete)
     */
    @DeleteMapping("")
    public ResponseEntity<ResponseDto<Map<String, Object>>> deleteLike(LikeRequest like,
                                                                       @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        like.setLikeMemberId(memberId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "좋아요가 취소되었습니다.", Map.of("like", likeService.deleteLike(like)))
        );
    }
}
