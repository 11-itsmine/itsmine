package com.sparta.itsmine.domain.social.kakao.service;

import static com.sparta.itsmine.global.security.JwtProvider.AUTHORIZATION_HEADER;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.itsmine.domain.refreshtoken.service.RefreshTokenService;
import com.sparta.itsmine.domain.user.dto.KakaoUserInfoDto;
import com.sparta.itsmine.domain.user.entity.User;
import com.sparta.itsmine.domain.user.repository.UserRepository;
import com.sparta.itsmine.domain.user.utils.UserRole;
import com.sparta.itsmine.global.security.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j(topic = "KAKAO Login")
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    // restTemplate를 수동으로 등록해야합니다.
    private final RestTemplate restTemplate;
    private final JwtProvider jwtProvider;
    @Autowired
    private RefreshTokenService refreshTokenService;

    public String kakaoLogin(String code, HttpServletResponse res) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String tokens = getToken(code);

        String KakaoAccessToken = tokens.split(" ")[0];

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(KakaoAccessToken);

        // 3. 필요 시 회원가입
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. return jwt
        String accessToken = jwtProvider.createAccessToken(kakaoUser.getUsername(), kakaoUser.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(kakaoUser.getUsername(), kakaoUser.getUserRole());

        res.setHeader(AUTHORIZATION_HEADER, accessToken);
        refreshTokenService.save(kakaoUser.getUsername(), refreshToken);

        res.setStatus(SC_OK);
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");

        return accessToken;
    }

    // 인가 코드 받는 메서드
    public String getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + code); // 토큰의 식별자
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "08a46b304d568ef454241721bcb6b981");
        body.add("redirect_uri", "http://52.79.213.8/oauth/callback/kakao");
        body.add("code", code); // 우리가 이전에 받아온 인가코드

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        // access_token 이라는 이름으로 되어 있는 토큰의 텍스트를 가져올 수 있습니다.
        String accessToken = jsonNode.get("access_token").asText();
        String refreshToken = jsonNode.get("refresh_token").asText();
        return accessToken + " " + refreshToken;
    }

    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // 토큰의 식별자
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // 추후에는 토큰 내역을 보고 가져 올 수 있다.
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        Long id = jsonNode.get("id").asLong();
        String username = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        log.info("카카오 사용자 정보: {}, {}, {}", id, username, email);
        return new KakaoUserInfoDto(id, username, email);
    }

    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        User kakaoUser = userRepository.findByKakaoId(kakaoId).orElse(null);

        if (kakaoUser == null) {
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
            String kakaoUsername = kakaoUserInfo.getUsername();
            User sameUsernameUser = userRepository.findByUsername(kakaoUsername).orElse(null);
            if (sameUsernameUser != null) {
                // 통합 과정
                kakaoUser = sameUsernameUser;
                // 기존 회원정보에 카카오 Id 추가
                kakaoUser.setKakaoId(kakaoId);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: kakao email
                String email = kakaoUserInfo.getEmail();

                kakaoUser = User.builder()
                        .username(kakaoUsername)
                        .name(kakaoUsername)
                        .nickname(kakaoUsername)
                        .kakaoId(kakaoId)
                        .role(UserRole.USER)
                        .encodedPassword(encodedPassword)
                        .email(email)
                        .address("")
                        .build();
            }

            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }
}
