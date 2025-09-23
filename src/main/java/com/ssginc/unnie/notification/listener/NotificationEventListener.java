package com.ssginc.unnie.notification.listener;

import com.ssginc.unnie.community.dto.comment.CommentCreatedEvent;
import com.ssginc.unnie.community.dto.comment.ReplyCreatedEvent;
import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.service.NotificationService;
import com.ssginc.unnie.notification.vo.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    /**
     * 댓글 생성 이벤트를 감지하여 알림을 보냅니다.
     */
    @EventListener
    @Transactional
    public void handleCommentCreatedEvent(CommentCreatedEvent event) {
        // 자기 게시물에 단 댓글은 알림을 보내지 않음
        if (event.getBoardAuthorId() != event.getCommentMemberId()) {

            String content = event.getCommentMemberNickname() + "님이 '" + event.getBoardTitle() + "' 게시글에 댓글을 남겼습니다.";

            NotificationMessage notification = NotificationMessage.builder()
                    .notificationMemberId(event.getBoardAuthorId()) // 알림 수신자: 게시글 작성자
                    .notificationType(NotificationType.COMMENT)
                    .notificationContents(content)
                    .notificationUrn("/community/board/" + event.getCommentBoardId())
                    .build();

            notificationService.send(notification);
        }
    }

    /**
     * 대댓글 생성 이벤트를 감지하여 알림을 보냅니다.
     */
    @EventListener
    @Transactional
    public void handleReplyCreatedEvent(ReplyCreatedEvent event) {
        // 자기 댓글에 단 대댓글은 알림을 보내지 않음
        if (event.getReceiverId() != event.getCommentMemberId()) {

            String content = event.getCommentMemberNickname() + "님이 회원님의 댓글에 답글을 남겼습니다.";

            NotificationMessage notification = NotificationMessage.builder()
                    .notificationMemberId(event.getReceiverId()) // 알림 수신자: 부모 댓글 작성자
                    .notificationType(NotificationType.REPLY)
                    .notificationContents(content)
                    .notificationUrn("/community/board/" + event.getCommentBoardId() + "#comment-" + event.getParentCommentId())
                    .build();

            notificationService.send(notification);
        }
    }
}