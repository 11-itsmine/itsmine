package com.sparta.itsmine.domain.auction.service;


import static com.sparta.itsmine.global.common.ResponseExceptionEnum.AUCTION_IMPOSSIBLE_BID;
import static com.sparta.itsmine.global.common.ResponseExceptionEnum.AUCTION_IMPOSSIBLE_BID_CAUSE_STATUS;
import static com.sparta.itsmine.global.common.ResponseExceptionEnum.AUCTION_NOT_FOUND;

import com.sparta.itsmine.domain.auction.dto.AuctionRequestDto;
import com.sparta.itsmine.domain.auction.dto.AuctionResponseDto;
import com.sparta.itsmine.domain.auction.dto.GetAuctionByMaxedBidPriceResponseDto;
import com.sparta.itsmine.domain.auction.dto.GetAuctionByProductResponseDto;
import com.sparta.itsmine.domain.auction.dto.GetAuctionByUserResponseDto;
import com.sparta.itsmine.domain.auction.entity.Auction;
import com.sparta.itsmine.domain.auction.repository.AuctionRepository;
import com.sparta.itsmine.domain.product.entity.Product;
import com.sparta.itsmine.domain.product.repository.ProductRepository;
import com.sparta.itsmine.domain.product.utils.ProductStatus;
import com.sparta.itsmine.domain.user.entity.User;
import com.sparta.itsmine.global.exception.Auction.AuctionImpossibleBid;
import com.sparta.itsmine.global.exception.Auction.AuctionImpossibleBidCauseStatus;
import com.sparta.itsmine.global.exception.Auction.AuctionNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;

    //입찰 생성(현재 입찰가(고른 상품에서 가장 높은 입찰가 or 상품 처음 입찰가) 이하이거나 즉시구매가를 넘어서 입찰하려하면 예외처리를 해줘야함,(조건은 나중에))
    //즉시구매가를 바로 입찰했을 때 바로 낙찰
    @Transactional
    public AuctionResponseDto createAuction(User user, Long productId,
            AuctionRequestDto requestDto) {
        Product product = product(productId);
        Integer auctionPrice = requestDto.getBidPrice();

        GetAuctionByMaxedBidPriceResponseDto maxedBidPrice = auctionRepository.findByProductBidPrice(
                productId);

        //현재 입찰가(고른 상품에서 가장 높은 입찰가 or 상품 처음 입찰가) 이하이거나 즉시구매가를 넘어서 입찰하려하면 예외처리
        if (auctionPrice < product.getCurrentPrice()
                && auctionPrice > product.getAuctionNowPrice()) {
            throw new AuctionImpossibleBid(AUCTION_IMPOSSIBLE_BID);
        }

        //현 최대 입찰가보다 낮으면 예외처리
        if (auctionRepository.existsByProductId(productId)) {
            if (auctionPrice <= maxedBidPrice.getBidPrice()) {
                throw new AuctionImpossibleBid(AUCTION_IMPOSSIBLE_BID);
            }
        }

        //현재 상품 상태가 경매 중이 아니면 예외처리
        if (!product.getStatus().equals(ProductStatus.BID)) {
            throw new AuctionImpossibleBidCauseStatus(AUCTION_IMPOSSIBLE_BID_CAUSE_STATUS);
        }

        Auction auction = new Auction(user, product, auctionPrice, product);

        auctionRepository.save(auction);

        //입찰가를 즉시구매가 만큼 썼으면 즉시 낙찰
        if (auctionPrice.equals(product.getAuctionNowPrice())) {
            successfulAuction(productId);
        }
        return new AuctionResponseDto(auction);

    }

    //유저 입찰 조회(queryDSL 조회)(각각 입찰한 상품 당 자신의 최대입찰가만 나오게끔)(유지보수 할때 더 좋음)
    public Page<GetAuctionByUserResponseDto> getAuctionByUser(User user, Pageable pageable) {
        Page<GetAuctionByUserResponseDto> auctions = auctionRepository.findAuctionAllByUserid(
                user.getId(), pageable);
        if (auctions == null) {
            throw new AuctionNotFoundException(AUCTION_NOT_FOUND);
        }

        return auctions;
    }

    //상품 입찰 조회(자신이 입찰한 상품의 자신의 최대입찰가만 나오게끔)
    public GetAuctionByProductResponseDto getAuctionByProduct(User user, Long productId) {
        GetAuctionByProductResponseDto productAuctions = auctionRepository.findByUserIdAndProductId(
                user.getId(), productId);
        if (productAuctions == null) {
            throw new AuctionNotFoundException(AUCTION_NOT_FOUND);
        }

        return productAuctions;
    }

//    낙찰 or 유찰은 상품 상태 확인하고 상품 관련된 입찰정보 삭제
//                (낙찰은 가격에 MAX 함수 이용해서 최대값만 남겨둠(이런 방식으로 패찰도 거름),(최대값이 여러개면 남은 레코드 중 가장 나중에 생긴 시간 것만 갖고오기))
//                (유찰은 걍 다 삭제)


    //낙찰(유저 ID상관없이 최대 가격만 남기고 다 삭제하고 남은 것만 출력(이론상 1개가 남긴 하는데 동시에 추가될 가능성이 있을 수 있으니 그에 대한 예외처리가 필요함(입찰 생성에서 해결함)))
    //조건: 즉시구매가를 바로 입찰했을 때(입찰 생성에서 해결해야함)
    //낙찰 시 상품 상태 SUCCESS_BID로 변경해야함
    @Transactional
    public void successfulAuction(Long productId) {
        List<Auction> auctions = auctionRepository.findAllByProductIdWithOutMaxPrice(productId);
        if (auctions == null) {
            throw new AuctionNotFoundException(AUCTION_NOT_FOUND);
        }

        auctionRepository.deleteAll(auctions);
        Product product = product(productId);
        product.turnStatus(ProductStatus.SUCCESS_BID);
        productRepository.save(product);

    }

    //유찰(상품ID로 조회해서 다 삭제(조건은 나중에))
    @Transactional
    public void avoidedAuction(Long productId) {
        auctionRepository.deleteAllByProductId(productId);
    }

    public Product product(Long productId) {
        return productRepository.findById(productId).orElseThrow();
    }

}

