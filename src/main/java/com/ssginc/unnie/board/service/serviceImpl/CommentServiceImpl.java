package com.ssginc.unnie.board.service.serviceImpl;

import com.ssginc.unnie.board.dto.comment.CommentCreateRequest;
import com.ssginc.unnie.board.mapper.CommentMapper;
import com.ssginc.unnie.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 댓글 기능 인터페이스의 구현체 클래스
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    /**
     * 댓글 작성 메서드
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createComment(CommentCreateRequest request, int memberId) {




        return 0;
    }
}
