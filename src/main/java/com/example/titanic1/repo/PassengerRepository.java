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

@Repository
public interface PassengerRepository extends JpaRepository<PassengerEntity, Long>, JpaSpecificationExecutor<PassengerEntity> {

    Page<PassengerEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);}