package com.example.titanic1.repo;

import com.example.titanic1.dto.PassengerEntityDto;
import com.example.titanic1.model.entity.PassengerEntity;
import com.example.titanic1.model.enums.Gender;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;


public interface PassengerRepository extends JpaRepository<PassengerEntity, Long> {
    List<PassengerEntity> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM PassengerEntity p WHERE p.survived = true")
    List<PassengerEntity> findAllSurvivors();

    @Query("SELECT p FROM PassengerEntity p WHERE p.age > 16")
    List<PassengerEntity> findAllAdults();

    @Query("SELECT p FROM PassengerEntity p WHERE p.gender = :gender")
    List<PassengerEntity> findAllByGender(@Param("gender") Gender gender);

    @Query("SELECT p FROM PassengerEntity p WHERE p.siblingsSpousesAboard = 0")
    List<PassengerEntity> findAllWithoutRelatives();

    Page<PassengerEntity> findAll(Specification<PassengerEntity> spec, Pageable sortedPageable);
}
