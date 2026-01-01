package com.ssginc.unnie.common.util.validation;

import com.ssginc.unnie.community.dto.board.BoardCreateRequest;
import com.ssginc.unnie.community.dto.board.BoardRequestBase;
import com.ssginc.unnie.community.dto.board.BoardUpdateRequest;
import com.ssginc.unnie.community.dto.board.BoardCategory;
import com.ssginc.unnie.common.exception.UnnieBoardException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.parser.BoardParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
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

        if (board instanceof BoardCreateRequest) {
            validateThumbnail(board.getBoardThumbnail());
        } else if (board instanceof BoardUpdateRequest){
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

        String plain = Jsoup.parse(content).text()
                .replace("\u00A0", " ")
                .replace("\u200B", "")
                .trim();
        int textLength = plain.codePointCount(0, plain.length());

        boolean hasImage = content.matches("(?is).*<img\\b[^>]*src\\s*=\\s*['\"][^'\"\\s][^'\">]*['\"][^>]*>.*");

        if (textLength == 0 && !hasImage) {
            log.error("본문(텍스트/이미지) 모두 없음");
            throw new UnnieBoardException(ErrorCode.BOARD_CONTENT_REQUIRED);
        }

        int MAX_TEXT_LEN = 4000;
        if (textLength > MAX_TEXT_LEN) {
            log.error("본문 텍스트 길이 초과: {}", textLength);
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
