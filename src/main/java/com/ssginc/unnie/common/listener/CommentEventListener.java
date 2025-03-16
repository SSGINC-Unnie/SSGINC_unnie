package com.ssginc.unnie.common.listener;

import com.ssginc.unnie.community.dto.comment.CommentCreatedEvent;
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
public class CommentEventListener {

    private final NotificationService notificationService;

    // 트랜잭션 커밋 후 이벤트 발행 실행되도록 보장
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentCreated(CommentCreatedEvent event) {
        NotificationMessage msg = NotificationMessage.builder()
                .notificationMemberId(event.getBoardAuthorId())
                .notificationType(NotificationType.COMMENT)
                .notificationContents(String.format("[%s]님이 [%s]글에 댓글을 남겼어요", event.getCommentMemberNickname(), event.getBoardTitle()))
                .notificationUrn(String.format("/community/board/%d", event.getCommentBoardId()))
                .build();

        notificationService.send(msg);

        log.info("댓글 이벤트리스너 실행 = {}", msg);
    }

}
