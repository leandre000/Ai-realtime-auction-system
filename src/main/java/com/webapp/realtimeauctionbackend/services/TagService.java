package com.webapp.realtimeauctionbackend.services;

import com.webapp.realtimeauctionbackend.models.Tag;
import com.webapp.realtimeauctionbackend.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Page<Tag> searchTags(String query, Pageable pageable) {
        return tagRepository.searchByName(query, pageable);
    }

    public List<Tag> getTagsByNames(List<String> names) {
        return tagRepository.findByNameIn(names);
    }

    @Transactional
    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
} 