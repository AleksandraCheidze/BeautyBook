package com.example.end.repository;

import com.example.end.models.Procedure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProcedureRepository extends JpaRepository<Procedure, Long> {

    @Query("SELECT p FROM Procedure p JOIN p.category c WHERE c.id = :categoryId")
    List<Procedure> findProceduresByCategoryId(@Param("categoryId") Long categoryId);

    boolean existsByNameAndCategoryId(String name, Long categoryId);

    boolean existsByName(String name);
}