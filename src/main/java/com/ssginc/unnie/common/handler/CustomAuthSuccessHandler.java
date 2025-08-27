package com.ssginc.unnie.common.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Spring Security가 기억해둔 원래 가려던 목적지(URL)가 있는지 확인
        RequestCache requestCache = new HttpSessionRequestCache();
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        // 2. 만약 목적지가 있다면, 그곳으로 리다이렉트
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            requestCache.removeRequest(request, response); // 세션에서 기억해둔 주소는 삭제
            response.sendRedirect(targetUrl);
            return;
        }

        // 3. 목적지가 없다면 (예: 헤더의 로그인 버튼을 직접 눌렀을 경우)
        // 사용자가 이전에 머물렀던 페이지(Referer)로 돌려보냄
        String prevPage = request.getHeader("Referer");
        if (prevPage != null && !prevPage.contains("/login")) {
            response.sendRedirect(prevPage);
        } else {
            // 돌아갈 곳이 마땅치 않으면 메인 페이지로 이동
            response.sendRedirect("/");
        }
    }
}