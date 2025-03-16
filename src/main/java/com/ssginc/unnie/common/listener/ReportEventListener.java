package com.ssginc.unnie.common.listener;

import com.ssginc.unnie.admin.dto.report.ReportCreatedEvent;
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
public class ReportEventListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReportCreated(ReportCreatedEvent event){

        NotificationMessage msg = NotificationMessage.builder()
                .notificationMemberId(event.getReceiverId())
                .notificationType(NotificationType.WARN)
                .notificationContents(String.format("\'[%s]\'글이 신고 접수 및 운영팀 검토 결과 삭제되었습니다. 올바른 이용을 부탁드립니다.", event.getTitle()))
                .notificationUrn("/")
                .build();


        notificationService.send(msg);

        log.info("경고 이벤트 발생 = {}",  msg);
    }

}
