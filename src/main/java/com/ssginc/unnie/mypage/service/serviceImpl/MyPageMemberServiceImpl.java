package com.ssginc.unnie.mypage.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieMemberException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.validation.MemberValidator;
import com.ssginc.unnie.member.vo.Member;
import com.ssginc.unnie.mypage.dto.member.MyPageMemberUpdateRequest;
import com.ssginc.unnie.mypage.mapper.MyPageMemberMapper;
import com.ssginc.unnie.mypage.service.MyPageMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 마이페이지 회원정보 수정 인터페이스 구현 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageMemberServiceImpl implements MyPageMemberService {

    private final MyPageMemberMapper myPageMemberMapper;
    private final PasswordEncoder passwordEncoder;
    private final MemberValidator memberValidator;

    @Override
    public int updateMember(MyPageMemberUpdateRequest updateRequest, Long memberId) {

        //닉네임, 전화번호, 새 비밀번호 유효성 검증
        memberValidator.validateUpdateRequest(updateRequest);

        updateRequest.setMemberId(memberId);

        //닉네임 중복 체크
        if (updateRequest.getMemberNickname() != null && !updateRequest.getMemberNickname().trim().isEmpty()){
            if (myPageMemberMapper.countByNickname(updateRequest.getMemberNickname(), memberId) > 0) {
                throw new UnnieMemberException(ErrorCode.DUPLICATE_NICKNAME);
            }
        }

        // 비밀번호 수정
        if (updateRequest.getNewPw() != null && !updateRequest.getNewPw().trim().isEmpty()) {
            //현재 비밀번호 입력 여부 확인
            if (updateRequest.getCurrentPw() == null) {
                throw new UnnieMemberException(ErrorCode.INVALID_PASSWORD);
            }
            // 현재 회원 정보 조회
            Member member = myPageMemberMapper.findMemberById(memberId);
            if (member == null) {
                throw new UnnieMemberException(ErrorCode.MEMBER_NOT_FOUND);
            }
            // 현재 비밀번호 일치 여부 확인
            // 입력한 현재 비밀번호와 db에 저장된 암호화 비밀번호 비교
            if (!passwordEncoder.matches(updateRequest.getCurrentPw(), member.getMemberPw())) {
                throw new UnnieMemberException(ErrorCode.INVALID_PASSWORD);
            }

            // 새 비밀번호와 새 비밀번호 확인 일치 여부
            if (!updateRequest.getNewPw().equals(updateRequest.getNewPwConfirm())) {
                throw new UnnieMemberException(ErrorCode.INVALID_PASSWORD);
            }
            //새 비밀번호 암호화
            updateRequest.setMemberPw(passwordEncoder.encode(updateRequest.getNewPw()));
        }
        int res = myPageMemberMapper.updateMember(updateRequest);
        if (res == 0) {
            throw new UnnieMemberException(ErrorCode.MEMBER_UPDATE_FAILED);
        }
        return res;
    }
}
