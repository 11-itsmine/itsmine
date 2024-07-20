package com.sparta.itsmine.domain.user.controller;


import static com.sparta.itsmine.global.common.ResponseCodeEnum.SUCCESS_LOGOUT;
import static com.sparta.itsmine.global.common.ResponseCodeEnum.USER_SUCCESS_SIGNUP;

import com.sparta.itsmine.domain.security.UserDetailsImpl;
import com.sparta.itsmine.domain.user.dto.SignupRequestDto;
import com.sparta.itsmine.domain.user.service.UserService;
import com.sparta.itsmine.global.common.HttpResponseDto;
import com.sparta.itsmine.global.common.ResponseUtils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<HttpResponseDto> signup(
            @RequestBody SignupRequestDto requestDto
    ) {
        String username = userService.signup(requestDto);
        return ResponseUtils.of(USER_SUCCESS_SIGNUP, username);
    }

    @GetMapping("/logout")
    public ResponseEntity<HttpResponseDto> logout(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        HttpServletResponse response
    ) {
        userService.logout(userDetails.getUsername(), response);
        return ResponseUtils.of(SUCCESS_LOGOUT);
    }
}
