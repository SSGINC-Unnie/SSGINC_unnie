package com.ssginc.unnie.notification.listener;

import com.ssginc.unnie.admin.dto.shop.ShopApprovedEvent;
import com.ssginc.unnie.community.dto.comment.CommentCreatedEvent;
import com.ssginc.unnie.community.dto.comment.ReplyCreatedEvent;
import com.ssginc.unnie.member.mapper.MemberMapper;
import com.ssginc.unnie.mypage.dto.shop.ShopRegistrationPendingEvent;
import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.service.NotificationService;
import com.ssginc.unnie.notification.vo.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final MemberMapper memberMapper;


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
                    .notificationMemberId(event.getReceiverId())
                    .notificationType(NotificationType.REPLY)
                    .notificationContents(content)
                    .notificationUrn("/community/board/" + event.getCommentBoardId() + "#comment-" + event.getParentCommentId())
                    .build();

            notificationService.send(notification);
        }
    }

    /**
     * 새로운 업체 등록 요청 이벤트를 감지하여 모든 관리자에게 알림을 보냅니다.
     */
    @EventListener
    @Transactional
    public void handleShopRegistrationPendingEvent(ShopRegistrationPendingEvent event) {
        // 1. 모든 관리자의 ID를 조회합니다.
        List<Long> adminIds = memberMapper.findAllAdminMemberIds();


        // 2. 알림 메시지를 생성합니다.
        String content = String.format("새로운 업체 '%s'의 승인 요청이 도착했습니다.", event.getShopName());

        // 3. 각 관리자에게 알림을 발송합니다.
        for (Long adminId : adminIds) {
            NotificationMessage notification = NotificationMessage.builder()
                    .notificationMemberId(adminId)
                    .notificationType(NotificationType.PERMISSION)
                    .notificationContents(content)
                    .notificationUrn("/admin/shop/approve/detail/" + event.getShopId()) // 관리자 페이지로 연결
                    .build();

            notificationService.send(notification);
        }
    }

    /**
     * 업체 승인 완료 이벤트를 감지하여 업체 주인에게 알림을 보냅니다.
     */
    @EventListener
    @Transactional
    public void handleShopApprovedEvent(ShopApprovedEvent event) {
        // 1. 알림 메시지를 생성합니다.
        String content = String.format("축하합니다! 회원님의 '%s' 업체 등록이 승인되었습니다.", event.getShopName());

        // 2. NotificationMessage 객체를 만듭니다.
        NotificationMessage notification = NotificationMessage.builder()
                .notificationMemberId(event.getReceiverId())
                .notificationType(NotificationType.PERMISSION)
                .notificationContents(content)
                .notificationUrn("/mypage/manager/shopdetail/" + event.getShopId())
                .build();

        // 3. 알림을 발송합니다.
        notificationService.send(notification);
    }
}