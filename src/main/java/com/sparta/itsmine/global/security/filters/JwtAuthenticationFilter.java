package com.sparta.itsmine.global.security.filters;

import static com.sparta.itsmine.global.security.JwtProvider.AUTHORIZATION_HEADER;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.itsmine.domain.refreshtoken.service.RefreshTokenService;
import com.sparta.itsmine.domain.user.dto.LoginRequestDto;
import com.sparta.itsmine.domain.user.repository.UserAdapter;
import com.sparta.itsmine.domain.user.utils.UserRole;
import com.sparta.itsmine.global.common.response.HttpResponseDto;
import com.sparta.itsmine.global.common.response.ResponseExceptionEnum;
import com.sparta.itsmine.global.exception.user.UserDeletedException;
import com.sparta.itsmine.global.security.JwtProvider;
import com.sparta.itsmine.global.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtProvider jwtProvider;
    @Autowired
    private UserAdapter userAdapter;
    @Autowired
    private RefreshTokenService refreshTokenService;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
        setFilterProcessesUrl("/v1/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        log.info("인증 시도");
        try {
            // json to object
            LoginRequestDto requestDto = new ObjectMapper()
                    .readValue(req.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res,
            FilterChain chain, Authentication authResult) throws IOException {
        log.info("인증 성공 및 JWT 생성");
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRole role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getUserRole();

        // 탈퇴 유저 확인
        if (userAdapter.isDeleted(username)) {
            throw new UserDeletedException(ResponseExceptionEnum.USER_DELETED);
        }

        String accessToken = jwtProvider.createAccessToken(username, role);
        String refreshToken = jwtProvider.createRefreshToken(username, role);

        // 헤더에 액세스 토큰 추가
        res.setHeader(AUTHORIZATION_HEADER, accessToken);

        // 쿠키에 액세스 토큰 추가
        // jwtProvider.addJwtToCookie(accessToken, res);

        // DB에 리프레시 토큰이 이미 있으면 수정, 없으면 저장
        refreshTokenService.save(username, refreshToken);

        log.info("로그인 성공 : {}", username);

        // 응답 메시지 작성
        res.setStatus(SC_OK);
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");

        // JSON 응답 생성
        String jsonResponse = new ObjectMapper().writeValueAsString(
                new HttpResponseDto(SC_OK, "로그인 성공", accessToken)
        );

        res.getWriter().write(jsonResponse);
    }

    /**
     * 로그인 실패
     */
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res,
            AuthenticationException failed) throws IOException {
        log.error("로그인 실패 : {}", failed.getMessage());

        // 응답 메시지 작성
        res.setStatus(SC_UNAUTHORIZED);
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");

        // JSON 응답 생성
        String jsonResponse = new ObjectMapper().writeValueAsString(
                new HttpResponseDto(SC_UNAUTHORIZED, "로그인 실패: " + failed.getMessage())
        );

        res.getWriter().write(jsonResponse);
    }

}
