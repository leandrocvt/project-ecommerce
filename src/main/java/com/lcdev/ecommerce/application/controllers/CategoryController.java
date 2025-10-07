package com.lcdev.ecommerce.application.controllers;

import com.lcdev.ecommerce.application.dto.category.CategoryTreeDTO;
import com.lcdev.ecommerce.application.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/tree")
    public ResponseEntity<List<CategoryTreeDTO>> getTree() {
        return ResponseEntity.ok(categoryService.getCategoryTree());
    }

}
