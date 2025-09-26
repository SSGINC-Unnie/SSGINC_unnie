package com.ssginc.unnie.like.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieLikeException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.validation.Validator;
import com.ssginc.unnie.like.dto.LikeCreatedEvent;
import com.ssginc.unnie.like.dto.LikeMemberDto;
import com.ssginc.unnie.like.dto.LikeRequest;
import com.ssginc.unnie.like.mapper.LikeMapper;
import com.ssginc.unnie.like.service.LikeService;
import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.dto.NotificationResponse;
import com.ssginc.unnie.notification.vo.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 좋아요 기능 관련 인터페이스 구현체
 */
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final Validator<LikeRequest> likeValidator; // 좋아요 유효성 검증 클래스
    private final ApplicationEventPublisher publisher;

    /**
     * 게시글 상세 화면 좋아요 여부 조회
     */
    @Override
    public boolean getLikeStatus(LikeRequest like) {

        likeValidator.validate(like);

        int res = likeMapper.getLikeStatus(like);

        return res == 1;
    }

    /**
     * 좋아요 추가 메서드
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createLike(LikeRequest like, LikeMemberDto loginUser) {

        like.setLikeMemberId(loginUser.getMemberId());

        likeValidator.validate(like);

        int res = likeMapper.createLike(like);

        if (res != 1) {
            throw new UnnieLikeException(ErrorCode.LIKE_CREATE_FAILED);
        }

        NotificationResponse notificationRes = getLikeTargetMemberInfoByTargetInfo(like);

        LikeCreatedEvent event = LikeCreatedEvent.builder()
                .receiverId(notificationRes.getReceiverId())
                .memberNickname(loginUser.getMemberNickname()) //  loginUser의 닉네임 사용
                .targetId(like.getLikeTargetId())
                .targetTitle(notificationRes.getTargetTitle())
                .type(like.getLikeTargetType())
                .build();

        if ("COMMENT".equals(like.getLikeTargetType())) {
            event.setTargetId(getBoardIdByCommentTargetId(like.getLikeId()));
        }

        publisher.publishEvent(event);

        return like.getLikeId();
    }

    /**
     * 좋아요 삭제(hardDelete)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteLike(LikeRequest like) {

        if (like.getLikeId() < 1) {
            throw new UnnieLikeException(ErrorCode.LIKE_NOT_FOUND);
        }

        checkLikeAndMemberId(like);

        int res = likeMapper.deleteLike(like);

        if (res != 1) {
            throw new UnnieLikeException(ErrorCode.LIKE_DELETE_FAILED);
        }




        return like.getLikeId();
    }

    /**
     * 좋아요 타켓 타입과 타겟 식별번호로 타겟 대상 작성자 정보 가져오기
     */
    @Override
    public NotificationResponse getLikeTargetMemberInfoByTargetInfo(LikeRequest like) {
        return likeMapper.getLikeTargetMemberInfoByTargetInfo(like);
    }

    /**
     * 댓글 좋아요 시 댓글 달린 게시글 식별 번호 가져오는 메서드
     */
    @Override
    public long getBoardIdByCommentTargetId(long likeId) {
        return likeMapper.getBoardIdByCommentTargetId(likeId);
    }

    /**
     * 좋아요 대상에 따라 알림 메세지 생성해주는 메서드
     * @return
     */
    @Override
    public NotificationMessage createNotificationMsg(LikeRequest like, NotificationResponse response) {

        NotificationMessage msg = NotificationMessage
                .builder()
                .notificationMemberId(response.getReceiverId())
                .notificationType(NotificationType.LIKE)
                .build();

        String type = like.getLikeTargetType();

        String content = null;
        String urn = null;

        switch (type) {
            case "BOARD" :
                content = String.format("[%s]님이 \'[%s]\'글을 좋아합니다", response.getReceiverNickname(), response.getTargetTitle());
                urn = String.format("/community/board/%d", like.getLikeTargetId());
                break;
            case "COMMENT" :
                content = String.format("[%s]님이 \'[%s]\'댓글을 좋아합니다", response.getReceiverNickname(), response.getTargetTitle());
                urn = String.format("/community/board/%d", this.getBoardIdByCommentTargetId(like.getLikeId()));
                break;
//            case "REVIEW" :
//                content = String.format("[%s]님이 \'[%s]\'리뷰를 좋아합니다", response.getReceiverId(), response.getTargetTitle());
//                urn = String.format("/community/board/%d", like.getLikeTargetId());
        }

        msg.setNotificationContents(content);
        msg.setNotificationUrn(urn);

        return msg;
    }

    /**
     * 좋아요 취소 권한 체크
     */
    private void checkLikeAndMemberId(LikeRequest like) {
        int res = likeMapper.checkLikeAndMemberId(like);

        if (res == -1) { // 좋아요 존재하지 않음
            throw new UnnieLikeException(ErrorCode.LIKE_NOT_FOUND);
        } else if (res == 0) { // 좋아요 취소 권한 없음
            throw new UnnieLikeException(ErrorCode.FORBIDDEN);
        }
    }


}
