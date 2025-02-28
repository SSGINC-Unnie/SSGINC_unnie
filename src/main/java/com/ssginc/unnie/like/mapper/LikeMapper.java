package com.ssginc.unnie.like.mapper;

import com.ssginc.unnie.like.dto.LikeRequest;
import com.ssginc.unnie.notification.dto.NotificationResponse;
import org.apache.ibatis.annotations.Mapper;

/**
 * 좋아요 기능 관련 Mapper
 */
@Mapper
public interface LikeMapper {
    int getLikeStatus(LikeRequest like);

    int createLike(LikeRequest like);

    int deleteLike(LikeRequest like);

    int checkLikeAndMemberId(LikeRequest like);

    NotificationResponse getLikeTargetMemberInfoByTargetInfo(LikeRequest like);

    long getBoardIdByCommentTargetId(long likeId);
}
