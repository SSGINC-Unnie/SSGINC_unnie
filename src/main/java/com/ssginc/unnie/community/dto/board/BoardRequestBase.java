package com.ssginc.unnie.community.dto.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class BoardRequestBase {
    private long boardId;
    private BoardCategory boardCategory;
    private String boardTitle;
    private String boardContents;
    private String boardThumbnail;
    private long boardAuthor;
}
