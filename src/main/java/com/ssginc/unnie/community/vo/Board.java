package com.ssginc.unnie.community.vo;

import com.ssginc.unnie.community.dto.board.BoardCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
    private long boardId; // 게시글 식별 번호
    private String boardTitle; //제목
    private BoardCategory boardCategory;//카테고리
    private String boardContents; //내용(html 형식)
    private LocalDateTime boardCreatedAt;//작성일시
    private LocalDateTime boardUpdatedAt;//수정일시
    private long boardViews;//조회수
    private String boardThumbnail;// 대표 이미지
    private long boardAuthor;//작성자 식별 번호
    private boolean boardIsActive;//활성화 여부
}
