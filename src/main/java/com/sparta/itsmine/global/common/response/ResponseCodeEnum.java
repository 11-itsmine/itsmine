package com.sparta.itsmine.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCodeEnum {
    // 유저
    SUCCESS_LOGIN(HttpStatus.OK, "로그인을 완료했습니다."),
    USER_SIGNUP_SUCCESS(HttpStatus.OK, "님의 회원가입을 완료 했습니다."),
    SUCCESS_LOGOUT(HttpStatus.OK, "로그아웃을 완료했습니다."),
    USER_SUCCESS_GET(HttpStatus.OK, "유저 조회를 완료 했습니다."),
    USER_DELETE_SUCCESS(HttpStatus.OK, "회원 탈퇴를 완료했습니다."),
    USER_RESIGN_SUCCESS(HttpStatus.OK, "회원 복구를 완료했습니다."),
    USER_UPDATE_SUCCESS(HttpStatus.OK, "유저 정보 수정을 완료했습니다."),
    USER_SUCCESS_SIGNUP(HttpStatus.OK, "님의 회원가입을 완료 했습니다."),
    REISSUE_ACCESS_TOKEN(HttpStatus.OK, "억세스 토큰 재발급을 완료했습니다."),
    USER_SUCCESS_LIST(HttpStatus.OK, "유저 리스트 입니다."),

    // 상품
    SUCCESS_SAVE_PRODUCT(HttpStatus.OK, "성공적으로 상품을 등록했습니다."),
    SUCCESS_TO_SEARCH_PRODUCTS(HttpStatus.OK, "고객님의 상품 조회가 완료되었습니다."),
    SUCCESS_TO_UPDATE(HttpStatus.OK, "상품 수정 완료!"),
    SUCCESS_DELETE_PRODUCT(HttpStatus.OK, "상품 삭제 완료!"),
    SUCCESS_TO_LIKE(HttpStatus.OK, "좋아요가 완료 되었습니다"),
    SUCCESS_TO_REMOVE_LIKE(HttpStatus.OK, "좋아요가 취소 되었습니다."),

    //Like
    SUCCESS_PRODUCTS_LIKE(HttpStatus.OK, "좋아요를 누른 상품 조회가 완료되었습니다."),

    // 카테고리
    SUCCESS_TO_MAKE_NEW_CATEGORY(HttpStatus.OK, "새로운 카테고리를 만드셨습니다."),
    CATEGORY_SUCCESS_GET(HttpStatus.OK, "카테고리 목록 조회에 성공하셨습니다."),
    //QnA
    SUCCESS_CREATE_QNA(HttpStatus.CREATED, "문의 내용이 등록 되었습니다."),
    SUCCESS_QNA_LIST(HttpStatus.OK, "문의 목록 조회"),
    NULL_QNA_LIST(HttpStatus.OK, "문의 내용은 없을수도 있습니다."),
    SUCCESS_GET_QNA(HttpStatus.OK, "문의 내용 입니다."),
    SUCCESS_UPDATE_QNA(HttpStatus.OK, "문의 수정 완료 했습니다."),
    SUCCESS_DELETE_QNA(HttpStatus.OK, "문의 삭제 완료 했습니다."),

    //댓글
    COMMENT_SUCCESS_CREATE(HttpStatus.OK, "댓글 작성이 완료 되었습니다."),
    COMMENT_SUCCESS_GET(HttpStatus.OK, "댓글 조회가 완료 되었습니다."),
    COMMENT_SUCCESS_UPDATE(HttpStatus.OK, "댓글 수정이 완료 되었습니다."),
    COMMENT_SUCCESS_DELETE(HttpStatus.OK, "댓글 삭제가 완료 되었습니다."),

    //경매
    AUCTION_SUCCESS_CREATE(HttpStatus.CREATED, "경매 생성이 완료 되었습니다."),
    AUCTION_SUCCESS_GET(HttpStatus.OK, "경매 조회가 완료 되었습니다."),
    AUCTION_SUCCESSFUL_BID(HttpStatus.OK, "낙찰 되었습니다."),
    AUCTION_SUCCESS_DELETE_AVOIDEDAUCTION(HttpStatus.OK, "유찰 되었습니다."),

    //채팅
    CHAT_SUCCESS_CREATE(HttpStatus.CREATED, "방 생성 완료"),
    CHAT_GET_MESSAGE_LIST(HttpStatus.OK, "채팅방에 들어왔습니다. 이전 채팅 내역 불러옵니다."),
    CHAT_GET_ROOM_LIST(HttpStatus.OK, "채팅방 리스트 입니다."),
    CHAT_SUCCESS_ROOM_LEAVE(HttpStatus.OK, "채팅방에서 나갔습니다"),
    CHAT_BLACKLIST_USER_ADD(HttpStatus.CREATED, "블랙 리스트 추가 했습니다."),
    CHAT_BLACKLIST_USER_CANCEL(HttpStatus.OK, "블랙 리스트 취소 했습니다."),

    //신고 관련
    REPORT_SUCCESS_CREATE(HttpStatus.CREATED, "신고가 접수 되었습니다."),
    REPORT_SUCCESS_UPDATE(HttpStatus.OK, "신고가 수정 되었습니다."),
    REPORT_SUCCESS_DELETE(HttpStatus.CREATED, "신고가 삭제 되었습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}