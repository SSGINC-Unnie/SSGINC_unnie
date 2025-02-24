package com.ssginc.unnie.like.service.serviceImpl;

import com.ssginc.unnie.common.util.validation.LikeValidator;
import com.ssginc.unnie.common.util.validation.Validator;
import com.ssginc.unnie.like.dto.LikeRequest;
import com.ssginc.unnie.like.mapper.LikeMapper;
import com.ssginc.unnie.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 좋아요 기능 관련 인터페이스 구현체
 */
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final Validator<LikeRequest> likeValidator; // 좋아요 유효성 검증 클래스

    /**
     * 게시글 상세 화면 좋아요 여부 조회
     */
    @Override
    public boolean getLikeStatus(LikeRequest like) {

        likeValidator.validate(like);

        int res = likeMapper.getLikeStatus(like);

        return res == 1;
    }


}
