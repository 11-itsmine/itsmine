package com.sparta.itsmine.domain.product.entity;

import static com.sparta.itsmine.domain.product.utils.ProductStatus.BID;

import com.sparta.itsmine.domain.auction.entity.Auction;
import com.sparta.itsmine.domain.category.entity.Category;
import com.sparta.itsmine.domain.images.entity.Images;
import com.sparta.itsmine.domain.product.dto.ProductCreateDto;
import com.sparta.itsmine.domain.product.utils.ProductStatus;
import com.sparta.itsmine.domain.user.entity.User;
import com.sparta.itsmine.global.common.TimeStamp;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Table(name = "product", indexes = {
        @Index(name = "idx_user_product_name", columnList = "user_id, productName"),
        @Index(name = "idx_product_deleted", columnList = "deletedAt")
})
@NoArgsConstructor
public class Product extends TimeStamp {

    /**
     * 컬럼 - 연관관계 컬럼을 제외한 컬럼을 정의합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;
    @Column(nullable = false)
    private String productName;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Integer startPrice;
    @Column
    private Integer currentPrice;//요청시 시작가만 받게 하기위해 요청 안해도 되게끔 만들었습니다
    @Column(nullable = false)
    private Integer auctionNowPrice;
    // 입찰, 낙찰, 유찰
    // 상품이 등록이 되는 즉시 입찰 시작이기 때문에 바로 입찰로 상태를 변경한다.
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(nullable = false)
    private LocalDateTime dueDate;
    private LocalDateTime deletedAt;

    /**
     * 연관관계 - Foreign Key 값을 따로 컬럼으로 정의하지 않고 연관 관계로 정의합니다.
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Images> imagesList = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Auction> auction = new ArrayList<>();

    private int likeCount;

    /**
     * 생성자 - 약속된 형태로만 생성가능하도록 합니다.
     */
    @Builder
    public Product(String productName, String description, Integer startPrice,
//따로 currentPrice를 받지 않습니다
            Integer auctionNowPrice, LocalDateTime dueDate, Category category) {
        this.productName = productName;
        this.description = description;
        this.startPrice = startPrice;
        this.currentPrice = startPrice;
        this.auctionNowPrice = auctionNowPrice;
        this.dueDate = dueDate;
        this.category = category;
        this.likeCount = 0;

        // set up initialized values
        this.status = BID;
    }

    /**
     * 연관관계 편의 메소드 - 반대쪽에는 연관관계 편의 메소드가 없도록 주의합니다.
     */

    public void assignUser(User user) {
        this.user = user;
    }

    /**
     * 서비스 메소드 - 외부에서 엔티티를 수정할 메소드를 정의합니다. (단일 책임을 가지도록 주의합니다.)
     */

    public void updateProduct(Product product, ProductCreateDto createDto, Integer hour) {
        this.productName = Optional.ofNullable(createDto.getProductName())
                .orElse(product.getProductName());
        this.description = Optional.ofNullable(createDto.getDescription())
                .orElse(product.getDescription());
        this.auctionNowPrice = Optional.ofNullable(createDto.getAuctionNowPrice())
                .orElse(product.getAuctionNowPrice());
        this.currentPrice = Optional.ofNullable(createDto.getStartPrice())//시작가 때문에 수정한 부분입니다
                .orElse(product.getCurrentPrice());
        if (hour != null) {
            this.dueDate = product.getDueDate().plusHours(hour);
        } else {
            this.dueDate = product.getDueDate();
        }
    }

    public void currentPriceUpdate(Integer bidPrice) {
        this.currentPrice = bidPrice;
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public void updateStatus(ProductStatus status) {
        this.status = status;
    }

    public void extendDueDateByHours(Integer hours) {
        this.dueDate = this.getDueDate().plusSeconds(hours);
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<String> getImageUrls() {
        return imagesList.stream()
                .map(Images::getImagesUrl)
                .collect(Collectors.toList());
    }

    public String getThumbnailUrl() {
        if (!imagesList.isEmpty()) {
            return imagesList.get(0).getImagesUrl();
        }
        return null; // or a default image URL
    }

    public void countUpdate(int count) {
        this.likeCount = this.likeCount + count;
    }
}
