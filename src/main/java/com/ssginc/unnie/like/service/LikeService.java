package com.ssginc.unnie.like.service;

import com.ssginc.unnie.like.dto.LikeRequest;
import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.dto.NotificationResponse;

/**
 * 좋아요 기능 관련 서비스
 */
public interface LikeService {
    boolean getLikeStatus(LikeRequest like);

    long createLike(LikeRequest like);

    long deleteLike(LikeRequest like);

    NotificationResponse getLikeTargetMemberInfoByTargetInfo(LikeRequest like);

    long getBoardIdByCommentTargetId(long likeId);

    NotificationMessage createNotificationMsg(LikeRequest like, NotificationResponse response);
}
