package com.sparta.itsmine.domain.product.controller;


import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.SUCCESS_DELETE_PRODUCT;
import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.SUCCESS_SAVE_PRODUCT;
import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.SUCCESS_TO_SEARCH_PRODUCTS;
import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.SUCCESS_TO_UPDATE;

import com.sparta.itsmine.domain.product.dto.ProductCreateDto;
import com.sparta.itsmine.domain.product.dto.ProductCreateRequestDto;
import com.sparta.itsmine.domain.product.dto.ProductResponseDto;
import com.sparta.itsmine.domain.product.service.ProductService;
import com.sparta.itsmine.global.common.response.HttpResponseDto;
import com.sparta.itsmine.global.common.response.PageableResponse;
import com.sparta.itsmine.global.common.response.ResponseUtils;
import com.sparta.itsmine.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<HttpResponseDto> createProduct(
            @RequestBody ProductCreateRequestDto productCreateRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ProductResponseDto product = productService.createProduct(
                productCreateRequestDto.getProductCreateDto(),
                productCreateRequestDto.getProductImagesRequestDto(),
                userDetails.getUser().getId());
        return ResponseUtils.of(SUCCESS_SAVE_PRODUCT, product);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<HttpResponseDto> getProduct(
            @PathVariable("productId") Long productId
    ) {
        return ResponseUtils.of(SUCCESS_TO_SEARCH_PRODUCTS, productService.getProduct(productId));
    }

    @GetMapping("/all")
    public ResponseEntity<HttpResponseDto> getAllProductsWithPage(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "price", required = false) String price,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        Page<ProductResponseDto> responseDto = productService.getAllProductsWithPage(page, size,
                category, price, search, sort);
        PageableResponse<ProductResponseDto> responseEntity = new PageableResponse<>(responseDto);
        return ResponseUtils.of(SUCCESS_TO_SEARCH_PRODUCTS, responseEntity);
    }

    @GetMapping("/user")
    public ResponseEntity<HttpResponseDto> getUserProductsWithPage(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Page<ProductResponseDto> responseDto = productService.getUserProductsWithPage(page, size,
                userDetails.getUser().getId());
        PageableResponse<ProductResponseDto> responseEntity = new PageableResponse<>(responseDto);
        return ResponseUtils.of(SUCCESS_TO_SEARCH_PRODUCTS, responseEntity);
    }

    @GetMapping("/likes")
    public ResponseEntity<HttpResponseDto> getAllLikeProductsWithPage(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Page<ProductResponseDto> responseDto = productService.getLikeProductsWithPage(page, size,
                userDetails.getUser().getId());
        PageableResponse<ProductResponseDto> responseEntity = new PageableResponse<>(responseDto);
        return ResponseUtils.of(SUCCESS_TO_SEARCH_PRODUCTS, responseEntity);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<HttpResponseDto> updateProduct(
            @RequestBody ProductCreateDto createDto,
            @PathVariable("productId") Long productId
    ) {
        productService.updateProduct(createDto, productId);
        return ResponseUtils.of(SUCCESS_TO_UPDATE);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<HttpResponseDto> deleteProduct(
            @PathVariable("productId") Long productId
    ) {
        productService.deleteProduct(productId);
        return ResponseUtils.of(SUCCESS_DELETE_PRODUCT);
    }
}
