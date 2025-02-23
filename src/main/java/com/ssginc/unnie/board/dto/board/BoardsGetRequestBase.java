package com.ssginc.unnie.board.dto.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 목록 조회 요청 공통 필드 담은 클래스
 */
@Getter
@Setter
@AllArgsConstructor
public abstract class BoardsGetRequestBase {
    private BoardCategory category;
    private String sort;
    private String searchType;
    private String search;
    private int page;
    private int pageSize;
}
