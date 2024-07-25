package com.example.titanic1.repo;

import com.example.titanic1.dto.PassengerEntityDto;
import com.example.titanic1.model.entity.PassengerEntity;
import com.example.titanic1.model.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<PassengerEntity, Long>, JpaSpecificationExecutor<PassengerEntity> {
    Page<PassengerEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);}