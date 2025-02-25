package com.ssginc.unnie.community.dto.board;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 시 게시글 목록 조회 요청 전용 DTO
 */
@Getter
@Setter
public class BoardsGetRequest extends BoardsGetRequestBase{

    private long memberId;

    @Builder
    public BoardsGetRequest(BoardCategory category, String sort, String searchType, String search, int page, int pageSize, long memberId) {
        super(category, sort, searchType, search, page, pageSize);
        this.memberId = memberId;
    }
}
