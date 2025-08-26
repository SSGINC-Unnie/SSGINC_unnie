package com.ssginc.unnie.community.dto.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BoardRequestBase {
    private Long boardId;
    private BoardCategory boardCategory;
    private String boardTitle;
    private String boardContents;
    private String boardThumbnail;
    private Long boardAuthor;
}
