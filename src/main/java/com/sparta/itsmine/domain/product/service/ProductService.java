package com.sparta.itsmine.domain.product.service;

import com.sparta.itsmine.domain.auction.service.AuctionService;
import com.sparta.itsmine.domain.category.entity.Category;
import com.sparta.itsmine.domain.images.dto.ProductImagesRequestDto;
import com.sparta.itsmine.domain.images.service.ImagesService;
import com.sparta.itsmine.domain.like.entity.Like;
import com.sparta.itsmine.domain.like.repository.LikeRepository;
import com.sparta.itsmine.domain.product.dto.ProductCreateDto;
import com.sparta.itsmine.domain.product.dto.ProductResponseDto;
import com.sparta.itsmine.domain.product.entity.Product;
import com.sparta.itsmine.domain.product.repository.ProductAdapter;
import com.sparta.itsmine.domain.product.repository.ProductRepository;
import com.sparta.itsmine.domain.product.scheduler.MessageSenderService;
import com.sparta.itsmine.domain.product.utils.ProductStatus;
import com.sparta.itsmine.domain.user.entity.User;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductAdapter adapter;
    private final AuctionService auctionService;
    private final ImagesService imagesService;
    private final MessageSenderService messageSenderService;
    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;

    public ProductResponseDto createProduct(ProductCreateDto createDto,
            ProductImagesRequestDto imagesRequestDto, Long userId) {
        User user = adapter.findByIdAndDeletedAtIsNull(userId);
        user.checkBlock();
        Category category = adapter.findCategoryByCategoryName(createDto.getCategoryName());
        adapter.existActiveProductByUserAndName(userId, createDto.getCategoryName());

        Product product = createDto.toEntity(category);
        product.assignUser(user);
        product.extendDueDateByHours(createDto.getDueDate());
        product.setCategory(category);

        Product newProduct = adapter.saveProduct(product);
        imagesService.createProductImages(imagesRequestDto, product);
        scheduleProductUpdate(newProduct);
        return new ProductResponseDto(newProduct, imagesRequestDto);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long productId) {
        return adapter.verifyProduct(productId);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProductsWithPage(int page, int size, String category,
            String price, String search, String sort) {

        if (sort == null) {
            sort = "createdAt"; // 기본 정렬 필드 설정
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Product> products = productRepository.findProducts(pageRequest, category, price,
                search, sort);
        return products.map(ProductResponseDto::new);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getUserProductsWithPage(int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Direction.ASC, "createdAt"));
        return adapter.findAllProducts(pageable, userId);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getLikeProductsWithPage(int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Direction.ASC, "createdAt"));
        Page<Like> likes = likeRepository.findAllByUserIdAndDeletedIsNull(userId, pageable);
        return likes.map(like -> new ProductResponseDto(like.getProduct()));
    }

    @Transactional
    public void updateProduct(ProductCreateDto createDto, Long productId) {
        Product product = adapter.getProduct(productId);
        product.updateProduct(product, createDto, createDto.getDueDate());
        adapter.saveProduct(product);
    }

    //상품 경매를 취소했을 때 유찰(상품 올린 걸 취소할 시 상태 변화는 유찰로 변해야 되지 않나)
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = adapter.getProduct(productId);
        product.markAsDeleted();
        product.updateStatus(ProductStatus.FAIL_BID);
        adapter.saveProduct(product);
        auctionService.avoidedAuction(productId);
    }


    private void scheduleProductUpdate(Product product) {
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        // 시작 시간과 종료 시간
        LocalDateTime startDate = product.getCreatedAt();
        LocalDateTime dueDate = product.getDueDate();

        // 현재 시간과 시작 시간 사이의 차이를 계산
        long delayMillis = Duration.between(now, dueDate).toMillis();

        // delayMillis가 0 이하일 경우 바로 처리
        if (delayMillis <= 0) {
            delayMillis = 1000; // 최소 1초 후에 상태를 처리하도록 설정
        }

        messageSenderService.sendMessage(product.getId(), delayMillis);
    }
}
