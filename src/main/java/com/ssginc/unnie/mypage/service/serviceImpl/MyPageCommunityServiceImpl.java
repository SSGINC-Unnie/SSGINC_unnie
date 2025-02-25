package com.ssginc.unnie.mypage.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.common.exception.UnnieBoardException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.community.mapper.BoardMapper;
import com.ssginc.unnie.community.mapper.CommentMapper;
import com.ssginc.unnie.mypage.dto.community.MyPageBoardsResponse;
import com.ssginc.unnie.mypage.dto.community.MyPageCommentsResponse;
import com.ssginc.unnie.mypage.service.MyPageCommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 마이페이지 나의 게시글/댓글 관련 인터페이스 구현 클래스
 */
@Service
@RequiredArgsConstructor
public class MyPageCommunityServiceImpl implements MyPageCommunityService {

    private final BoardMapper boardMapper;
    private final CommentMapper commentMapper;

    private final int PAGE_SIZE = 10;

    /**
     * 나의 게시글 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public PageInfo<MyPageBoardsResponse> getMyBoards(long memberId, int page) {

        if (page < 1) {
            throw new UnnieBoardException(ErrorCode.PAGE_OUT_OF_RANGE);
        }

        PageHelper.startPage(page, PAGE_SIZE);

        List<MyPageBoardsResponse> myBoards = boardMapper.getMyBoards(memberId);

        return new PageInfo<>(myBoards);
    }

    @Override
    @Transactional(readOnly = true)
    public PageInfo<MyPageCommentsResponse> getMyComments(long memberId, int page) {
        if (page < 1) {
            throw new UnnieBoardException(ErrorCode.PAGE_OUT_OF_RANGE);
        }

        PageHelper.startPage(page, 10);

        List<MyPageCommentsResponse> myComments = commentMapper.getMyComments(memberId);

        return new PageInfo<>(myComments);
    }
}
