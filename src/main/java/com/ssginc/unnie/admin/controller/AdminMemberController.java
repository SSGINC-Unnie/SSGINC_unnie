package com.ssginc.unnie.admin.controller;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.admin.dto.member.AdminMemberDetailResponse;
import com.ssginc.unnie.admin.dto.member.AdminMemberResponse;
import com.ssginc.unnie.admin.service.AdminMemberService;
import com.ssginc.unnie.common.util.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/member")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    /**
     * 회원 전체 리스트 조회
     */
    @GetMapping("")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getAllMembers(
        @RequestParam(defaultValue = "1") int page,        // 페이지 번호
        @RequestParam(defaultValue = "5") int pageSize) { // 페이지 크기

        PageInfo<AdminMemberResponse> memberList = adminMemberService.findAllMembers(page, pageSize);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "모든 회원 조회에 성공하였습니다.", Map.of("member", memberList))
        );
    }

    /**
     * 회원 상세정보 조회
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getMemberDetail(@PathVariable int memberId) {
        AdminMemberDetailResponse memberDetail = adminMemberService.findMemberDetail(memberId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "회원 상세 조회에 성공하였습니다.", Map.of("memberDetail", memberDetail)));
    }
}
