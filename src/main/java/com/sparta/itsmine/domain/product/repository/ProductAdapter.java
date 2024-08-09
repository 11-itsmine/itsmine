package com.sparta.itsmine.domain.product.repository;

import static com.sparta.itsmine.global.common.response.ResponseExceptionEnum.CATEGORY_NOT_FOUND;
import static com.sparta.itsmine.global.common.response.ResponseExceptionEnum.PRODUCT_IN_DATE;
import static com.sparta.itsmine.global.common.response.ResponseExceptionEnum.PRODUCT_NOT_FOUND;
import static com.sparta.itsmine.global.common.response.ResponseExceptionEnum.USER_NOT_FOUND;

import com.sparta.itsmine.domain.category.entity.Category;
import com.sparta.itsmine.domain.category.repository.CategoryRepository;
import com.sparta.itsmine.domain.product.dto.ProductResponseDto;
import com.sparta.itsmine.domain.product.entity.Product;
import com.sparta.itsmine.domain.user.entity.User;
import com.sparta.itsmine.domain.user.repository.UserRepository;
import com.sparta.itsmine.global.exception.DataNotFoundException;
import com.sparta.itsmine.global.exception.product.ProductInDateException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductAdapter {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public User findByIdAndDeletedAtIsNull(Long userId) {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new DataNotFoundException(USER_NOT_FOUND));
    }

    public Category findCategoryByCategoryName(String categoryName) {
        return categoryRepository.findCategoryByCategoryName(
                        categoryName)
                .orElseThrow(() -> new DataNotFoundException(CATEGORY_NOT_FOUND));
    }

    public void existActiveProductByUserAndName(Long userId, String categoryName) {
        Optional<Product> existingProduct = productRepository.existActiveProductByUserAndName(
                userId, categoryName);

        if (existingProduct.isPresent()) {
            throw new ProductInDateException(PRODUCT_IN_DATE);
        }
    }


    public ProductResponseDto verifyProduct(Long productId) {
        Product product = productRepository.findActiveProductById(productId)
                .orElseThrow(() -> new DataNotFoundException(PRODUCT_NOT_FOUND));
        return new ProductResponseDto(product);
    }

    public Page<ProductResponseDto> findAllProducts(Pageable pageable, Long userId) {
        return productRepository.findAllByUserIdAndDeletedAtIsNull(userId, pageable)
                .map(ProductResponseDto::new);
    }
    
    public Product getProduct(Long productId) {
        return productRepository.findActiveProductById(productId)
                .orElseThrow(() -> new DataNotFoundException(PRODUCT_NOT_FOUND));
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
