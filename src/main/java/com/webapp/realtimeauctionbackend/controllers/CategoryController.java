package com.webapp.realtimeauctionbackend.controllers;

import com.webapp.realtimeauctionbackend.models.Category;
import com.webapp.realtimeauctionbackend.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/search")
    public ResponseEntity<Page<Category>> searchCategories(
            @RequestParam String query,
            Pageable pageable) {
        return ResponseEntity.ok(categoryService.searchCategories(query, pageable));
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }
} 