package com.ssginc.unnie.community.controller;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.community.dto.board.BoardCategory;
import com.ssginc.unnie.community.dto.board.BoardsGuestGetResponse;
import com.ssginc.unnie.community.dto.board.SearchType;
import com.ssginc.unnie.community.dto.board.SortType;
import com.ssginc.unnie.community.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("community/board")
public class BoardViewController {

    private final BoardService boardService;

    @GetMapping
    public String getBoardListView(Model model) {
        model.addAttribute("activePage", "community");
        return "community/boardList";
    }

    // 새 글 작성 페이지
    @GetMapping("/write")
    public String getBoardWriteView(Model model) {
        model.addAttribute("activePage", "community");

        return "community/boardWrite";
    }

    // 게시글 상세 조회 페이지
    @GetMapping("/{boardId}")
    public String getBoardDetailView(@PathVariable String boardId, Model model) {
        model.addAttribute("boardId", boardId);
        model.addAttribute("activePage", "community");

        return "community/boardDetail";
    }

    // 게시글 수정 페이지
    @GetMapping("/{boardId}/edit")
    public String getBoardEditView(@PathVariable String boardId, Model model) {
        model.addAttribute("boardId", boardId);
        model.addAttribute("activePage", "community");

        return "community/boardEdit";
    }

}
