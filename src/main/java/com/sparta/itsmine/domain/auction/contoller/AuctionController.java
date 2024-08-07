package com.sparta.itsmine.domain.auction.contoller;

import static com.sparta.itsmine.domain.product.utils.ProductStatus.SUCCESS_BID;
import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.AUCTION_SUCCESSFUL_BID;
import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.AUCTION_SUCCESS_CREATE;
import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.AUCTION_SUCCESS_GET;

import com.sparta.itsmine.domain.auction.dto.AuctionProductResponseDto;
import com.sparta.itsmine.domain.auction.dto.AuctionRequestDto;
import com.sparta.itsmine.domain.auction.dto.AuctionResponseDto;
import com.sparta.itsmine.domain.auction.service.AuctionService;
import com.sparta.itsmine.global.common.response.HttpResponseDto;
import com.sparta.itsmine.global.common.response.ResponseUtils;
import com.sparta.itsmine.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    //구매자 입찰 생성
    @PostMapping("/v1/products/{productId}/auctions")
    public ResponseEntity<HttpResponseDto> createAuction(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long productId,
            @RequestBody AuctionRequestDto requestDto) {
        AuctionResponseDto responseDto = auctionService.createAuction(userDetails.getUser(),
                productId,
                requestDto);
        return ResponseUtils.of(
                responseDto.getStatus().equals(SUCCESS_BID) ? AUCTION_SUCCESSFUL_BID
                        : AUCTION_SUCCESS_CREATE, responseDto);

    }

    //유저(구매자(본인)) 입찰 조회(QueryDSL)
    @GetMapping("/auctions")
    public ResponseEntity<HttpResponseDto> getAuctionByUserToList(
            @AuthenticationPrincipal UserDetailsImpl userDetails, Pageable pageable) {
        Page<AuctionProductResponseDto> responseDto = auctionService.getAuctionByUser(
                userDetails.getUser(), pageable);
        return ResponseUtils.of(AUCTION_SUCCESS_GET, responseDto);
    }

    //유저(구매자(본인)) 상품 입찰 조회
    @GetMapping("/products/{productId}/auctions")
    public ResponseEntity<HttpResponseDto> getAuctionByProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId) {
        AuctionProductResponseDto responseDto = auctionService.getAuctionByProduct(
                userDetails.getUser(), productId);
        return ResponseUtils.of(AUCTION_SUCCESS_GET, responseDto);
    }

}
