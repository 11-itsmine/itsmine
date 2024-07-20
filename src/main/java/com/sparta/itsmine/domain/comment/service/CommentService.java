package com.sparta.itsmine.domain.comment.service;

import com.sparta.itsmine.domain.comment.dto.CommentRequestDto;
import com.sparta.itsmine.domain.comment.dto.CommentResponseDto;
import com.sparta.itsmine.domain.comment.entity.Comment;
import com.sparta.itsmine.domain.comment.repository.CommentRepository;
import com.sparta.itsmine.domain.qnaJH.QnaRepositoryJH;
import com.sparta.itsmine.domain.qnaJH.entity.QnaJH;
import com.sparta.itsmine.global.common.ResponseExceptionEnum;
import com.sparta.itsmine.global.exception.comment.CommentNotFoundException;
import com.sparta.itsmine.global.exception.comment.QnaNotFoundExceptionJH;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final QnaRepositoryJH qnaRepositoryJH;
//  private final ProductRepository productRepository;

    // 댓글 작성
    @Transactional
    public void addComment(Long qnaId, UserDetails userDetails, CommentRequestDto commentRequestDto) {

        // 판매자만 댓글 작성 가능
//        equalsSeller(userDetails.getUsername());

        QnaJH qna = getQna(qnaId);

        Comment comment = new Comment(commentRequestDto,qna);

        commentRepository.save(comment);
    }

    //    // 판매자 검증
//    public void equalsSeller(String currentUsername) {
//        if (!productRepository.findByUsername(currentUsername).equals(currentUsername)) {
//            throw new CommentEqualSellerException(ResponseExceptionEnum.COMMENT_EQUAL_SELLER);
//        }
//    }

    // QnA 가져오기
    public QnaJH getQna(Long qnaId) {
        return qnaRepositoryJH.findById(qnaId).orElseThrow(
                () -> new QnaNotFoundExceptionJH(ResponseExceptionEnum.QNAJH_NOT_FOUND)
        );
    }
}
