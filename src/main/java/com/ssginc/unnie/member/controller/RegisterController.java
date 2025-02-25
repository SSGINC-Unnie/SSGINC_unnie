package com.ssginc.unnie.member.controller;


import com.ssginc.unnie.common.util.SimpleResponseDto;
import com.ssginc.unnie.member.dto.MemberRegisterRequest;
import com.ssginc.unnie.member.service.RegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
