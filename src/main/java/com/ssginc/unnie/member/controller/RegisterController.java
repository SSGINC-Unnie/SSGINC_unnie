package com.ssginc.unnie.member.controller;


import com.ssginc.unnie.common.util.SimpleResponseDto;
import com.ssginc.unnie.member.dto.MemberRegisterRequest;
import com.ssginc.unnie.member.service.RegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 회원가입 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class RegisterController {

    private final RegisterService registerService;

    //회원가입 폼에서 제출된 데이터 처리
    @PostMapping("/register")
    public ResponseEntity<SimpleResponseDto> registerMember(@RequestBody MemberRegisterRequest memberRequest){
        int result = registerService.insertMember(memberRequest);
        SimpleResponseDto responseDto = new SimpleResponseDto(HttpStatus.CREATED.value(),"회원가입에 성공했습니다.");
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 이메일 중복 체크
     */
    @GetMapping("/emailCheck")
    public ResponseEntity<Boolean> checkMemberEmail(String email) {
        // count가 0이면 사용 가능한 이메일
        int count = registerService.checkMemberEmail(email);
        boolean isUsable = (count == 0);
        return ResponseEntity.ok(isUsable);
    }

    /**
     * 닉네임 중복 체크
     */
    @GetMapping("/nicknameCheck")
    public ResponseEntity<Boolean> checkMemberNickname(String nickname) {
        int count = registerService.checkMemberNickname(nickname);
        boolean isUsable = (count == 0);
        return ResponseEntity.ok(isUsable);
    }
}
