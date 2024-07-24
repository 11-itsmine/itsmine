package com.sparta.itsmine.domain.user.controller;


import static com.sparta.itsmine.global.common.ResponseCodeEnum.SUCCESS_LOGOUT;
import static com.sparta.itsmine.global.common.ResponseCodeEnum.USER_DELETE_SUCCESS;
import static com.sparta.itsmine.global.common.ResponseCodeEnum.USER_RESIGN_SUCCESS;
import static com.sparta.itsmine.global.common.ResponseCodeEnum.USER_SUCCESS_GET;
import static com.sparta.itsmine.global.common.ResponseCodeEnum.USER_SIGNUP_SUCCESS;
import static com.sparta.itsmine.global.common.ResponseCodeEnum.USER_UPDATE_SUCCESS;
import static com.sparta.itsmine.global.common.ResponseUtils.*;

import com.sparta.itsmine.domain.user.dto.ProfileUpdateRequestDto;
import com.sparta.itsmine.global.security.UserDetailsImpl;
import com.sparta.itsmine.domain.user.dto.SignupRequestDto;
import com.sparta.itsmine.domain.user.dto.UserResponseDto;
import com.sparta.itsmine.domain.user.service.UserService;
import com.sparta.itsmine.global.common.HttpResponseDto;
import com.sparta.itsmine.global.common.ResponseUtils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        return of(USER_SIGNUP_SUCCESS, username);
    }

    @GetMapping("/logout")
    public ResponseEntity<HttpResponseDto> logout(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        HttpServletResponse response
    ) {
        userService.logout(userDetails.getUsername(), response);
        return of(SUCCESS_LOGOUT);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<HttpResponseDto> getUser(
        @PathVariable Long userId
    ) {
        UserResponseDto response = userService.getUser(userId);
        return of(USER_SUCCESS_GET, response);
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<HttpResponseDto> withdraw(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        userService.withdraw(userDetails.getUser());
        return of(USER_DELETE_SUCCESS);
    }

    @PutMapping("/resign/{userId}")
    public ResponseEntity<HttpResponseDto> resign(
        @PathVariable Long userId
    ) {
        userService.resign(userId);
        return of(USER_RESIGN_SUCCESS);
    }

    @PutMapping("/update")
    public ResponseEntity<HttpResponseDto> update(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody ProfileUpdateRequestDto updateDto
    ) {
        userService.update(userDetails.getUser(), updateDto);
        return of(USER_UPDATE_SUCCESS);
    }
}
