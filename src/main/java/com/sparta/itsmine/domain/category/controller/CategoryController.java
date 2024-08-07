package com.sparta.itsmine.domain.category.controller;


import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.CATEGORY_SUCCESS_GET;
import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.SUCCESS_TO_MAKE_NEW_CATEGORY;

import com.sparta.itsmine.domain.category.dto.CategoryDto;
import com.sparta.itsmine.domain.category.dto.CategoryResponseDto;
import com.sparta.itsmine.domain.category.entity.Category;
import com.sparta.itsmine.domain.category.service.CategoryService;
import com.sparta.itsmine.global.common.response.HttpResponseDto;
import com.sparta.itsmine.global.common.response.ResponseUtils;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<HttpResponseDto> createCategory(
            @RequestBody CategoryDto categoryName
    ) {
        categoryService.createCategory(categoryName);
        return ResponseUtils.of(SUCCESS_TO_MAKE_NEW_CATEGORY);
    }

    @GetMapping
    public ResponseEntity<HttpResponseDto> getCategory() {
        List<CategoryResponseDto> responseDto = categoryService.getCategory();
        return ResponseUtils.of(CATEGORY_SUCCESS_GET, responseDto);
    }
}
