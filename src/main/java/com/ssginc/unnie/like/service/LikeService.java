package com.ssginc.unnie.like.service;

import com.ssginc.unnie.like.dto.LikeRequest;

/**
 * 좋아요 기능 관련 서비스
 */
public interface LikeService {
    boolean getLikeStatus(LikeRequest like);

    long createLike(LikeRequest like);

    long deleteLike(LikeRequest like);
}
