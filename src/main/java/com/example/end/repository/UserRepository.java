package com.example.end.repository;


import com.example.end.models.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByIdAndRole(Long id, User.Role role);

    List<User> findAllByRole(User.Role role);

    @Query("SELECT u FROM User u JOIN u.categories c WHERE c.id = :categoryId")
    List<User> findUsersByCategoryId(@Param("categoryId")  Long categoryId);
    /**
     * Finds a user by ID with preloaded categories and procedures.
     *
     * @param id user ID
     * @return user with loaded categories and procedures
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.categories LEFT JOIN FETCH u.procedures WHERE u.id = :id")
    Optional<User> findByIdWithDetails(@Param("id") Long id);

    /**
     * Finds all users with MASTER role with preloaded categories and procedures.
     *
     * @return list of masters with loaded categories and procedures
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.categories LEFT JOIN FETCH u.procedures WHERE u.role = 'MASTER'")
    List<User> findAllMastersWithDetails();

    /**
     * Finds all users with preloaded categories and procedures.
     *
     * @return list of all users with loaded categories and procedures
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.categories LEFT JOIN FETCH u.procedures")
    List<User> findAllWithDetails();

    /**
     * Finds users by category ID with preloaded categories and procedures.
     *
     * @param categoryId category ID
     * @return list of users related to the given category with loaded details
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.categories c LEFT JOIN FETCH u.procedures WHERE c.id = :categoryId")
    List<User> findUsersByCategoryIdWithDetails(@Param("categoryId") Long categoryId);

}


