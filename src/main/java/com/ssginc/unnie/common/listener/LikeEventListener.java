package com.ssginc.unnie.common.listener;

import com.ssginc.unnie.community.dto.comment.ReplyCreatedEvent;
import com.ssginc.unnie.like.dto.LikeCreatedEvent;
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
public class LikeEventListener {

    private final NotificationService notificationService;

    // 트랜잭션 커밋 후 이벤트 발행 실행되도록 보장
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeCreated(LikeCreatedEvent event) {

        NotificationMessage msg = NotificationMessage
                .builder()
                .notificationMemberId(event.getReceiverId())
                .notificationType(NotificationType.LIKE)
                .build();

        String type = event.getType();

        String content = null;
        String urn = String.format("/community/board/%d", event.getTargetId());

        //            case "REVIEW" :
        //                content = String.format("[%s]님이 \'[%s]\'리뷰를 좋아합니다", response.getReceiverId(), response.getTargetTitle());
        //                urn = String.format("/community/board/%d", like.getLikeTargetId());
        content = switch (type) {
            case "BOARD" -> String.format("[%s]님이 \'[%s]\'글을 좋아합니다", event.getMemberNickname(), event.getTargetTitle());
            case "COMMENT" ->
                    String.format("[%s]님이 \'[%s]\'댓글을 좋아합니다", event.getMemberNickname(), event.getTargetTitle());
            default -> content;
        };

        msg.setNotificationContents(content);
        msg.setNotificationUrn(urn);

        notificationService.send(msg);

        log.info("좋아요 이벤트리스너 실행 = {}", msg);
    }
}
