package com.ssginc.unnie.member.service.serviceImpl;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.exception.UnnieLoginException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.ssginc.unnie.member.vo.Member;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class MemberDetailsServiceImpl implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // username 파라미터에는 로그인 시 사용된 이메일이 들어옴.
        Member member = memberMapper.selectMemberByEmail(email);
        if (member == null) {
            throw new UnnieLoginException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 커스텀 UserDetails 객체(MemberPrincipal)를 반환함
        return new MemberPrincipal(member);
    }
}
