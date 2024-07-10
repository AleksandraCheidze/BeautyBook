package com.example.end.repository;

import com.example.end.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface ReviewRepository extends JpaRepository<Review, Long> {


    @Query("SELECT r FROM Review r JOIN r.master m WHERE m.id = :masterId")
    List<Review> findByMasterId(@Param("masterId") Long masterId);
}

