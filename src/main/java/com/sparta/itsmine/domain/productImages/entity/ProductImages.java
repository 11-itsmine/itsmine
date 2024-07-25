package com.sparta.itsmine.domain.productImages.entity;

import com.sparta.itsmine.domain.product.entity.Product;
import com.sparta.itsmine.domain.user.entity.User;
import com.sparta.itsmine.global.common.TimeStamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class ProductImages extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "images_url", joinColumns = @JoinColumn(name = "product_images_id"))
    @Column(name = "images_url", nullable = false)
    private List<String> imagesUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ProductImages(List<String> imagesUrl, Product product, User user) {
        this.imagesUrl = imagesUrl;
        this.product = product;
        this.user = user;
    }
}