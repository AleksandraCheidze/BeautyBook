package com.example.end.repository;

import com.example.end.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r JOIN r.master m WHERE m.id = :masterId")
    List<Review> findByMasterId(@Param("masterId") Long masterId);
}

