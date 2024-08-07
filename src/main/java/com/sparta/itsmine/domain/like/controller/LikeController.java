package com.sparta.itsmine.domain.like.controller;


import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.SUCCESS_PRODUCTS_LIKE;

import com.sparta.itsmine.domain.like.dto.LikeResponseDto;
import com.sparta.itsmine.domain.like.service.LikeService;
import com.sparta.itsmine.global.common.response.HttpResponseDto;
import com.sparta.itsmine.global.common.response.ResponseUtils;
import com.sparta.itsmine.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users/products/{productId}")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/likes")
    public ResponseEntity<HttpResponseDto> createLike(
            @PathVariable("productId") Long productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseUtils.of(likeService.createLike(productId, userDetails.getUser()));
    }

    @GetMapping("/likes")
    public ResponseEntity<HttpResponseDto> getLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("productId") Long productId) {
        LikeResponseDto responseDto = likeService.getLike(productId, userDetails.getUser());
        return ResponseUtils.of(SUCCESS_PRODUCTS_LIKE, responseDto);
    }
}
