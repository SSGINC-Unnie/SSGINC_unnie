package com.ssginc.unnie.notification.controller;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.notification.service.NotificationService;
import com.ssginc.unnie.notification.vo.Notification;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    /**
     * 로그인 시 SSE 연결
     */
    @GetMapping(value = "/subscribe/{id}", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable long id,
                               @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId,
                                HttpServletResponse response) {

        response.setHeader("X-Accel-Buffering", "no"); // NGINX PROXY 에서의 필요설정 불필요한 버퍼링방지

        SseEmitter emitter = notificationService.subscribe(id, lastEventId);

        return emitter;
    }

    @GetMapping("")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getMyNotifications(@AuthenticationPrincipal MemberPrincipal memberPrincipal){

//        long memberId = memberPrincipal.getMemberId();
        long memberId = memberPrincipal.getMemberId();

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "알림 조회 성공", Map.of("notifications", notificationService.getMyNotificationsByMemberId(memberId)))
        );
    }

    // '모든 알림 보기' 페이지를 위한 API
    @GetMapping("/all")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getAllMyNotifications(
            @RequestParam(defaultValue = "1") int page,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        long memberId = memberPrincipal.getMemberId();

        PageInfo<Notification> notificationPage = notificationService.getAllMyNotificationsByMemberId(memberId, page);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "전체 알림 조회 성공", Map.of("notifications", notificationPage))
        );
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ResponseDto<Void>> markAsRead(@PathVariable long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "알림 읽음 처리 성공", null)
        );
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ResponseDto<Void>> markAllAsRead(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        notificationService.markAllAsRead(memberId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "모든 알림 읽음 처리 성공", null)
        );
    }

    @GetMapping("/count")
    public ResponseEntity<ResponseDto<Map<String, Object>>> countUnreadNotifications(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        int count = notificationService.countUnreadNotifications(memberId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "안 읽은 알림 수 조회 성공", Map.of("count", count))
        );
    }

    @DeleteMapping("")
    public ResponseEntity<ResponseDto<Void>> deleteNotifications(
            @RequestBody(required = false) Map<String, List<Long>> payload,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        if (payload != null && payload.containsKey("ids")) {
            List<Long> ids = payload.get("ids");
            notificationService.deleteNotifications(ids);
            return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "선택 알림 삭제 성공", null));
        } else {

            long memberId = memberPrincipal.getMemberId();
            notificationService.deleteAllNotifications(memberId);
            return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "전체 알림 삭제 성공", null));
        }
    }



}
