package com.ssginc.unnie.common.listener;

import com.ssginc.unnie.community.dto.comment.CommentCreatedEvent;
import com.ssginc.unnie.community.dto.comment.ReplyCreatedEvent;
import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.service.NotificationService;
import com.ssginc.unnie.notification.vo.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReplyEventListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReplyCreated(ReplyCreatedEvent event) {

        String content = String.format("%s님이 회원님의 댓글에 답글을 남겼습니다.", event.getCommentMemberNickname());

        NotificationMessage msg = NotificationMessage.builder()
                .notificationMemberId(event.getReceiverId())
                .notificationType(NotificationType.REPLY)
                .notificationContents(content) // 수정된 내용 적용
                .notificationUrn(String.format("/community/board/%d#comment-%d", event.getCommentBoardId(), event.getParentCommentId())) // 원본 댓글로 바로 이동하도록 URL 수정
                .build();

        notificationService.send(msg);

        log.info("대댓글 이벤트리스너 실행 = {}", msg);
    }

}
