package com.webapp.realtimeauctionbackend.repositories;

import com.webapp.realtimeauctionbackend.models.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Tag> searchByName(@Param("query") String query, Pageable pageable);

    List<Tag> findByNameIn(List<String> names);
} 