package com.example.end.repository;


import com.example.end.models.User;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByIdAndRole(Long id, User.Role role);
    
    @Query("SELECT DISTINCT u FROM User u WHERE u.role = :role")
    List<User> findAllByRole(@Param("role") User.Role role);

    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.categories c " +
           "LEFT JOIN FETCH u.procedures " +
           "WHERE u.role = 'MASTER' AND u.isActive = true " +
           "AND EXISTS (SELECT 1 FROM u.categories cat WHERE cat.id = :categoryId)")
    List<User> findUsersByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.categories " +
           "LEFT JOIN FETCH u.procedures " +
           "LEFT JOIN FETCH u.reviewsAsMaster " +
           "LEFT JOIN FETCH u.portfolioPhotos " +
           "WHERE u.role = 'MASTER' AND u.isActive = true")
    List<User> findAllActiveMasters();
}


