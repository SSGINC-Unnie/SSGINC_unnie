package com.ssginc.unnie.mypage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/mypage/community")
public class MyPageCommunityViewController {

    @GetMapping("/posts")
    public String myPostsPage() {
        return "mypage/community/getMyposts";
    }

    @GetMapping("/comments")
    public String myCommentsPage() {
        return "mypage/community/getMycomments";
    }
}