package com.ssginc.unnie.mypage.service;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.mypage.dto.community.MyPageBoardsResponse;
import com.ssginc.unnie.mypage.dto.community.MyPageCommentsResponse;

/**
 * 마이페이지 나의 게시글/댓글 관련 인터페이스
 */
public interface MyPageCommunityService {
    PageInfo<MyPageBoardsResponse> getMyBoards(long memberId, int page);

    PageInfo<MyPageCommentsResponse> getMyComments(long memberId, int page);
}
