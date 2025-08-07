package com.webapp.realtimeauctionbackend.repositories;

import com.webapp.realtimeauctionbackend.models.Person;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Person p WHERE p.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT p FROM Person p WHERE p.email = :email")
    Optional<Person> findByEmail(@Param("email") String email);

    @Query("SELECT p FROM Person p WHERE p.id = :id")
    Optional<Person> findById(@Param("id") Long id);

    @Query("SELECT p FROM Person p WHERE p.role = :role")
    Optional<Person> findByRole(@Param("role") String role);

    @Query("SELECT COUNT(p) FROM Person p WHERE p.lastLogin > :date")
    long countByLastLoginAfter(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(p) FROM Person p WHERE p.createdAt < :date")
    long countByCreatedAtBefore(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(p) FROM Person p WHERE p.createdAt BETWEEN :start AND :end")
    long countByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(p) FROM Person p WHERE SIZE(p.bids) > 0")
    long countByBidsIsNotEmpty();
}