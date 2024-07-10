package com.example.end.repository;


import com.example.end.models.Procedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProcedureRepository extends JpaRepository<Procedure, Long> {
    Optional<Procedure> findByName(String name);



    @Query("SELECT p FROM Procedure p JOIN p.category c WHERE c.id = :categoryId")
    List<Procedure> findProceduresByCategoryId(@Param("categoryId") Long categoryId);
}