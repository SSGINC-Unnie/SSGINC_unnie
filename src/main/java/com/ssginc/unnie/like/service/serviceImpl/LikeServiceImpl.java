package com.ssginc.unnie.like.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieLikeException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.validation.Validator;
import com.ssginc.unnie.like.dto.LikeRequest;
import com.ssginc.unnie.like.mapper.LikeMapper;
import com.ssginc.unnie.like.service.LikeService;
import lombok.RequiredArgsConstructor;
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
    public long createLike(LikeRequest like) {

        likeValidator.validate(like);

        int res = likeMapper.createLike(like);

        if (res != 1) {
            throw new UnnieLikeException(ErrorCode.LIKE_CREATE_FAILED);
        }

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
