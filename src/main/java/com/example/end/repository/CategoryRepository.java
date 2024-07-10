package com.example.end.repository;


import com.example.end.models.Category;
import com.example.end.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {



    Set<Category> findByUserMasterId(Long userId);
}

