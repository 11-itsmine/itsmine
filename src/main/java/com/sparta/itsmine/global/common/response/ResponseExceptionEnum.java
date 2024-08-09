package com.sparta.itsmine.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseExceptionEnum {
    // 유저
    REFRESH_TOKEN_UNAVAILABLE(HttpStatus.BAD_REQUEST, "유효하지 않은 리프레쉬토큰 입니다."),
    USER_FAIL_SIGNUP(HttpStatus.BAD_REQUEST, "회원가입에 실패했습니다."),
    USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "중복된 아이디 입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    USER_DELETED(HttpStatus.UNAUTHORIZED, "탈퇴한 사용자입니다"),
    USER_NOT_DELETED(HttpStatus.BAD_REQUEST, "탈퇴되지 않은 사용자입니다."),
    NOT_FOUND_AUTHENTICATION_INFO(HttpStatus.BAD_REQUEST, "사용자 정보가 일치하지 않습니다. 다시 시도해 주세요 :)"),
    INVALID_REFRESHTOKEN(HttpStatus.NOT_FOUND, "유효하지 않은 리프레쉬 토큰입니다."),
    FAIL_TO_CHANGE_ROLE(HttpStatus.BAD_REQUEST, "Role 변경을 실패했습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    USER_ERROR(HttpStatus.BAD_REQUEST, "유저 오류 발생"),
    USER_BEN(HttpStatus.BAD_REQUEST, "벤당한 유저 입니다"),


    // 상품
    PRODUCT_IN_DATE(HttpStatus.BAD_REQUEST, "해당 상품이 입찰 경매 진행 중입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."),
    USER_MISMATCH(HttpStatus.BAD_REQUEST, "유저가 일치하지 않습니다"),

    //QNA
    QNA_ERROR(HttpStatus.BAD_REQUEST, "문의 오류 발생"),
    QNA_NOT_FOUND(HttpStatus.NOT_FOUND, "문의 내용이 존재 하지 않습니다."),
    QNA_USER_NOT_VALID(HttpStatus.BAD_REQUEST, "문의 내용 작성자가 아닙니다."),

    // 카테고리
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),

    // 댓글
    COMMENT_EQUAL_SELLER(HttpStatus.BAD_REQUEST, "해당 상품 판매자만 접근이 가능합니다"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "해당 문의사항에는 이미 댓글이 작성 되어 있습니다."),

    //경매
    AUCTION_DENIED_BID(HttpStatus.BAD_REQUEST, "판매자 자신이 경매에 참여할 순 없습니다."),
    AUCTION_IMPOSSIBLE_BID(HttpStatus.BAD_REQUEST,
            "상품의 입찰가보다 낮거나 즉시구매가보다 높은 입찰가입니다 입찰 금액을 확인해주세요."),
    AUCTION_NOT_FOUND(HttpStatus.NOT_FOUND, "입찰 기록을 찾을 수 없습니다."),
    AUCTION_IMPOSSIBLE_BID_CAUSE_STATUS(HttpStatus.BAD_REQUEST,
            "이미 낙찰됐거나 유찰된 상품입니다."),
    // 좋아요
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 하고 있지 않는 상품입니다."),

    //채팅
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방이 없습니다."),
    CHAT_ROOM_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "이미 나간 유저 입니다."),
    CHAT_NOT_ONE_TO_ONE(HttpStatus.NOT_FOUND, "나간 유저가 존재하여 채팅이 불가능 합니다."),
    CHAT_BLACKLIST_USER(HttpStatus.NOT_FOUND, "블랙 리스트에 등록된 유저 입니다."),
    CHAT_ROOM_SELF_CREATE(HttpStatus.NOT_FOUND, "혼자서 채팅방을 생성할수 없습니다."),

    // 상품 이미지
    INVALID_URL_EXCEPTION(HttpStatus.OK, "유효하지 않은 URL 경로입니다. 올바른 URL 을 입력해주세요."),
    //신고
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "신고가 존재하지 않습니다."),
    REPORT_COMPLETE_STATUS(HttpStatus.BAD_REQUEST, "이미 처리한 신고 입니다."),
    REPORT_MANAGER_STATUS(HttpStatus.BAD_REQUEST, "매니저가 아닙니다."),
    REPORT_NOT_ROLE(HttpStatus.BAD_REQUEST, "권한이 없습니다"),
    SELF_NOT_BLOCK(HttpStatus.BAD_REQUEST, "본인을 차단 할수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
