package com.ssginc.unnie.board.service.serviceImpl;

import com.ssginc.unnie.board.dto.BoardRequest;
import com.ssginc.unnie.board.mapper.BoardMapper;
import com.ssginc.unnie.board.service.BoardService;
import com.ssginc.unnie.board.vo.BoardCategory;
import com.ssginc.unnie.common.util.parser.BoardParser;
import com.ssginc.unnie.common.util.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final Validator<BoardRequest> boardValidator; // 유효성 검증 인터페이스

    private final BoardMapper boardMapper;

    private static final Set<String> CATEGORY_SET = Arrays.stream(BoardCategory.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createBoard(BoardRequest boardRequest) {

        // => 미리 파싱해서 boardRequest 에 넣음

        log.info("boardRequest: {}", boardRequest);

        // BoardParser 사용하여 HTML 파싱 후 검증
        BoardParser parser = new BoardParser(boardRequest.getBoardContents());

        String thumbnail = parser.extractFirstImage();

        boardRequest = BoardRequest.builder()
                .boardTitle(boardRequest.getBoardTitle())    // 기존 제목 유지
                .boardCategory(boardRequest.getBoardCategory()) // 기존 카테고리 유지
                .boardContents(boardRequest.getBoardContents()) // 기존 본문 유지 (HTML 그대로 저장)
                .boardAuthor(boardRequest.getBoardAuthor())  // 기존 작성자 유지
                .boardThumbnail(thumbnail) // 새로 추출한 썸네일 저장
                .build();

        boardValidator.validate(boardRequest);

        int res = boardMapper.insertBoard(boardRequest);

        return String.valueOf(res);
    }
}
