package com.ssginc.unnie.member.service.serviceImpl;


import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.member.dto.GoogleResponse;
import com.ssginc.unnie.member.dto.MemberRegisterRequest;
import com.ssginc.unnie.member.dto.NaverResponse;
import com.ssginc.unnie.member.dto.OAuth2Response;
import com.ssginc.unnie.member.mapper.MemberMapper;
import com.ssginc.unnie.member.vo.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final PasswordEncoder passwordEncoder;
    private final MemberMapper memberMapper;
    //private final AuthServiceImpl authService;
    //private final OAuthServiceImpl oauthService;

    //OAuth2로그인 후 사용자 정보 처리
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // OAuth2 제공자로부터 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("OAuth2User Attributes: {}", attributes);

        OAuth2Response oAuth2Response = null;
        //구글 로그인
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청");
            oAuth2Response = new GoogleResponse(attributes);
        }
        //네이버 로그인
        else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            System.out.println("네이버 로그인 요청");
            oAuth2Response = new NaverResponse(attributes);
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            //oAuth2Response = new KakaoResponse(attributes);
        } else {
            return null;
        }

        String email = oAuth2Response.getEmail();   // 이메일
        String password = passwordEncoder.encode(UUID.randomUUID().toString());
        String name = oAuth2Response.getName();
        String role = "ROLE_USER";

        // db에서 사용자 찾기 (이메일로 회원정보 조회)
        Member member = memberMapper.selectMemberByEmail(email);

        // 첫로그인
        if (member == null) {
            //return new MemberPrincipal(null, oAuth2Response, attributes);
            //회원가입 처리
            //존재하지 않는 회원이라면 새로운 회원으로 등록
//            member = Member.builder()
//                    .memberEmail(email)
//                    .memberPw(password)
//                    .memberName(name)
//                    .memberRole(role)
//                    .build();
//            memberMapper.insertMember(member);
        }


        // 기존 회원이면 로그인 처리
        log.info("회원 정보 조회 완료: {}", member);
        return new MemberPrincipal(oAuth2Response); //OAuth2user 리턴
    }
}
