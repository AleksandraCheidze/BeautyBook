package com.example.end.repository;

import com.example.end.models.PortfolioPhoto;
import com.example.end.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioPhotoRepository extends JpaRepository<PortfolioPhoto, Long> {
    List<PortfolioPhoto> findByUserId(Long userId);
}
