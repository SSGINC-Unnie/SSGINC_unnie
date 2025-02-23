package com.ssginc.unnie.board.dto.board;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 비회원 게시글 목록 조회 요청 전용 DTO
 */
@Getter
@Setter
public class BoardsGuestGetRequest extends BoardsGetRequestBase{

    @Builder
    public BoardsGuestGetRequest(BoardCategory category, String sort, String searchType, String search, int page, int pageSize) {
        super(category, sort, searchType, search, page, pageSize);
    }
}
