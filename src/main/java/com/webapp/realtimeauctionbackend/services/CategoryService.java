package com.webapp.realtimeauctionbackend.services;

import com.webapp.realtimeauctionbackend.models.Category;
import com.webapp.realtimeauctionbackend.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Page<Category> searchCategories(String query, Pageable pageable) {
        return categoryRepository.searchByName(query, pageable);
    }

    public List<Category> getCategoriesByNames(List<String> names) {
        return categoryRepository.findByNameIn(names);
    }

    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
} 