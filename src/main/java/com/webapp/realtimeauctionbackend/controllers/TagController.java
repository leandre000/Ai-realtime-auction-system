package com.webapp.realtimeauctionbackend.controllers;

import com.webapp.realtimeauctionbackend.models.Tag;
import com.webapp.realtimeauctionbackend.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping("/search")
    public ResponseEntity<Page<Tag>> searchTags(
            @RequestParam String query,
            Pageable pageable) {
        return ResponseEntity.ok(tagService.searchTags(query, pageable));
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        return ResponseEntity.ok(tagService.createTag(tag));
    }
} 