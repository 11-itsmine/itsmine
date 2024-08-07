package com.sparta.itsmine.domain.comment.controller;

import com.sparta.itsmine.domain.comment.dto.AddCommentResponseDto;
import com.sparta.itsmine.domain.comment.dto.CommentRequestDto;
import com.sparta.itsmine.domain.comment.dto.CommentResponseDto;
import com.sparta.itsmine.domain.comment.service.CommentService;
import com.sparta.itsmine.global.common.response.HttpResponseDto;
import com.sparta.itsmine.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.*;
import static com.sparta.itsmine.global.common.response.ResponseUtils.of;

@RestController
@RequestMapping("/v1/qnas/{qnaId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping
    public ResponseEntity<HttpResponseDto> addComment(
            @PathVariable Long qnaId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid CommentRequestDto commentRequestDto) {

        AddCommentResponseDto comment = commentService.addComment(qnaId,userDetails.getUser(),commentRequestDto);
        return of(COMMENT_SUCCESS_CREATE, comment);
    }

    // 댓글 조회
    @GetMapping
    public ResponseEntity<HttpResponseDto> getComment(
            @PathVariable Long qnaId) {

        CommentResponseDto comment = commentService.getComment(qnaId);
        return of(COMMENT_SUCCESS_GET, comment);
    }

    // 댓글 수정
    @PatchMapping
    public ResponseEntity<HttpResponseDto> updateComment(
            @PathVariable Long qnaId,
            @RequestBody @Valid CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        commentService.updateComment(qnaId,commentRequestDto,userDetails.getUser());
        return of(COMMENT_SUCCESS_UPDATE);
    }

    // 댓글 삭제
    @DeleteMapping
    public ResponseEntity<HttpResponseDto> deleteComment(
            @PathVariable Long qnaId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        commentService.deleteComment(qnaId,userDetails.getUser());
        return of(COMMENT_SUCCESS_DELETE);
    }
}
