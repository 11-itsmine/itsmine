package com.sparta.itsmine.domain.auction.service;

import static com.sparta.itsmine.domain.product.utils.ProductStatus.SUCCESS_BID;

import com.sparta.itsmine.domain.auction.dto.AuctionProductResponseDto;
import com.sparta.itsmine.domain.auction.dto.AuctionRequestDto;
import com.sparta.itsmine.domain.auction.dto.AuctionResponseDto;
import com.sparta.itsmine.domain.auction.entity.Auction;
import com.sparta.itsmine.domain.auction.repository.AuctionAdapter;
import com.sparta.itsmine.domain.auction.repository.AuctionRepository;
import com.sparta.itsmine.domain.product.entity.Product;
import com.sparta.itsmine.domain.product.repository.ProductAdapter;
import com.sparta.itsmine.domain.product.repository.ProductRepository;
import com.sparta.itsmine.domain.product.scheduler.MessageSenderService;
import com.sparta.itsmine.domain.user.entity.User;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final MessageSenderService messageSenderService;
    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;
    private final AuctionAdapter adapter;
    private final ProductAdapter productAdapter;

    @Transactional
    public AuctionResponseDto createAuction(User user, Long productId,
            AuctionRequestDto requestDto) {
        Product product = productAdapter.getProduct(productId);
        Integer bidPrice = requestDto.getBidPrice();

        Auction auction = createAuctionEntity(user, product, bidPrice);

        checkAuctionValidity(auction, product, bidPrice);

        currentPriceUpdate(bidPrice, product);
        auctionRepository.save(auction);

        if (bidPrice.equals(product.getAuctionNowPrice())) {
            successfulAuction(productId);
        } else {
            scheduleMessage(productId, product.getDueDate());
        }

        return new AuctionResponseDto(auction);
    }

    private Auction createAuctionEntity(User user, Product product, Integer bidPrice) {
        return new Auction(user, product, bidPrice, product.getStatus());
    }

    private void checkAuctionValidity(Auction auction, Product product, Integer bidPrice) {
        auction.checkStatus(product.getStatus());
        auction.checkBidPrice(bidPrice);
        auction.checkCurrentPrice(bidPrice, product.getCurrentPrice());
    }

    private void scheduleMessage(Long productId, LocalDateTime dueDate) {
        long delayMillis = calculateDelay(dueDate);
        messageSenderService.sendMessage(productId, delayMillis);
    }

    private long calculateDelay(LocalDateTime dueDate) {
        return dueDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                - System.currentTimeMillis();
    }

    @Transactional
    public void successfulAuction(Long productId) {
        allDeleteBid(productId);
        turnToSuccessBidProduct(productId);
        messageSenderService.sendMessage(productId, 0); // 즉시 메시지 전송
    }

    private void turnToSuccessBidProduct(Long productId) {
        Product product = productAdapter.getProduct(productId);
        product.updateStatus(SUCCESS_BID);
        productRepository.save(product);
    }

    public void allDeleteBid(Long productId) {
        auctionRepository.deleteAllByProductId(productId);
    }

    @Transactional
    public void avoidedAuction(Long productId) {
        allDeleteBid(productId);
    }

    public void currentPriceUpdate(Integer bidPrice, Product product) {
        product.currentPriceUpdate(bidPrice);
        productRepository.save(product);
    }

    public Page<AuctionProductResponseDto> getAuctionByUser(User user, Pageable pageable) {
        return adapter.findAuctionAllByUserid(user.getId(), pageable);
    }

    public AuctionProductResponseDto getAuctionByProduct(User user, Long productId) {
        return adapter.findByUserIdAndProductId(user.getId(), productId);
    }
}
