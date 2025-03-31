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
    List<User> findAllByRole(User.Role role);

    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.categories c " +
           "LEFT JOIN FETCH u.procedures " +
           "LEFT JOIN FETCH u.portfolioPhotos " +
           "LEFT JOIN FETCH u.reviewsAsMaster " +
           "WHERE c.id = :categoryId")
    List<User> findUsersByCategoryId(@Param("categoryId") Long categoryId);

}


