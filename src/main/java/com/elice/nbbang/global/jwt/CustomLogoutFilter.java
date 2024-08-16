package com.elice.nbbang.global.jwt;

import com.elice.nbbang.domain.auth.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        log.info("CustomLogoutFilter - Received request: {} {}", request.getMethod(), request.getRequestURI());

        //path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/api\\/logout$")) {
            log.info("CustomLogoutFilter - Request URI does not match /logout. Passing request along the filter chain.");
            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            log.info("CustomLogoutFilter - Request method is not POST. Passing request along the filter chain.");
            filterChain.doFilter(request, response);
            return;
        }

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    log.info("CustomLogoutFilter - Found refresh token in cookies: {}", refresh);
                    break;
                }
            }
        }

        //refresh null check
        if (refresh == null) {
            log.warn("CustomLogoutFilter - No refresh token found in cookies.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
            log.info("CustomLogoutFilter - Refresh token is valid.");
        } catch (ExpiredJwtException e) {
            log.warn("CustomLogoutFilter - Refresh token has expired.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            log.warn("CustomLogoutFilter - Token category is not 'refresh'.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            log.warn("CustomLogoutFilter - Refresh token not found in the database.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //로그아웃 진행
        log.info("CustomLogoutFilter - Logging out user, deleting refresh token from database and clearing cookies.");

        //Refresh 토큰 DB에서 제거
        refreshRepository.deleteByRefresh(refresh);

        //Refresh 토큰 Cookie 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        log.info("CustomLogoutFilter - Refresh token cookie cleared.");

        // Access 토큰 Cookie 값 삭제
        Cookie accessTokenCookie = new Cookie("access", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);
        log.info("CustomLogoutFilter - Access token cookie cleared.");

        response.setStatus(HttpServletResponse.SC_OK);
        log.info("CustomLogoutFilter - Logout process completed successfully.");
    }
}