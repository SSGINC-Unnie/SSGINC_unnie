package com.ssginc.unnie.common.util.validation;

import com.ssginc.unnie.community.dto.board.BoardRequestBase;
import com.ssginc.unnie.community.dto.board.BoardUpdateRequest;
import com.ssginc.unnie.community.dto.board.BoardCategory;
import com.ssginc.unnie.common.exception.UnnieBoardException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.parser.BoardParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 게시글 유효성 검증 클래스
 */

@Slf4j  // 로그 추가
@Component
public class BoardValidator implements Validator<BoardRequestBase> {

    // BoardCategory를 Set으로 저장 (유효한 카테고리 목록) -> O(1) 조회
    private static final Set<String> CATEGORY_SET = Arrays.stream(BoardCategory.values())
            .map(BoardCategory::getDescription)
            .collect(Collectors.toSet());

    @Override
    public boolean validate(BoardRequestBase board) {
        validateTitle(board.getBoardTitle());
        validateCategory(board.getBoardCategory());
        validateContent(board.getBoardContents());
        validateThumbnail(board.getBoardThumbnail());

        if (board instanceof BoardUpdateRequest){
            validateBoardId(((BoardUpdateRequest) board).getBoardId());
        }

        return true;
    }

    /**
     * 게시글 제목 검증
     */
    private void validateTitle(String title) {
        if (title == null || title.isEmpty()) {
            log.error("게시글 제목이 누락되었습니다.");
            throw new UnnieBoardException(ErrorCode.BOARD_TITLE_REQUIRED);
        }
    }

    /**
     * 게시글 카테고리 검증
     */
    private void validateCategory(BoardCategory category) {
        if (category == null) {
            log.error("게시글 카테고리가 누락되었습니다.");
            throw new UnnieBoardException(ErrorCode.BOARD_CATEGORY_REQUIRED);
        }

        // 수정: category.getDescription() 사용 (올바른 방식)
        if (!CATEGORY_SET.contains(category.getDescription())) {
            log.error("유효하지 않은 카테고리: {}", category.getDescription());
            throw new UnnieBoardException(ErrorCode.BOARD_NOT_INVALID);
        }
    }

    /**
     * 게시글 본문 검증
     */
    private void validateContent(String content) {
        if (content == null || content.isEmpty()) {
            log.error("게시글 내용이 누락되었습니다.");
            throw new UnnieBoardException(ErrorCode.BOARD_CONTENT_REQUIRED);
        }

        // BoardParser 사용하여 HTML 파싱 후 검증
        BoardParser parser = new BoardParser(content);

        int textLength = parser.getContentTextLength();

        if (textLength == 0) {
            log.error("<div id='content'> 태그 내부에 본문이 없습니다.");
            throw new UnnieBoardException(ErrorCode.BOARD_CONTENT_REQUIRED);
        }

        if (textLength < 10 || textLength > 800) {
            log.error("게시글 내용 길이 제한 위반: 현재 길이 = {}", textLength);
            throw new UnnieBoardException(ErrorCode.BOARD_CONTENT_LENGTH_INVALID);
        }
    }

    private void validateThumbnail(String thumbnail) {
        // 이미지 첨부 여부 검증
        if ( thumbnail == null) {
            throw new UnnieBoardException(ErrorCode.BOARD_FILE_REQUIRED);
        }
    }

    private void validateBoardId(long id){
        if ( id <= 0 ) {
            throw new UnnieBoardException(ErrorCode.NULL_POINTER_ERROR);
        }
    }
}
