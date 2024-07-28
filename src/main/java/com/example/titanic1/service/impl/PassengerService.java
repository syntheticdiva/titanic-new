package com.example.titanic1.service.impl;

import com.example.titanic1.dto.PassengerEntityDto;
import com.example.titanic1.dto.PassengerStatistics;
import com.example.titanic1.mapper.PassengerMapper;
import com.example.titanic1.model.entity.PassengerEntity;
import com.example.titanic1.model.enums.Gender;
import com.example.titanic1.repo.PassengerRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PassengerService {

    private static final Logger logger = LoggerFactory.getLogger(PassengerService.class);

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private PassengerMapper passengerMapper;

    @Cacheable(value = "passengers", sync = true)
    public Page<PassengerEntityDto> getFilteredAndSortedPassengers(
            Pageable pageable,
            String searchName,
            Boolean survived,
            Boolean adult,
            Gender gender,
            Boolean noRelatives) {

        Specification<PassengerEntity> spec = Specification.where(null);

        spec = Optional.ofNullable(searchName)
                .filter(s -> !s.isEmpty())
                .map(PassengerSpecifications::nameContains)
                .map(spec::and)
                .orElse(spec);
        spec = Optional.ofNullable(survived)
                .map(PassengerSpecifications::isSurvived)
                .map(spec::and)
                .orElse(spec);
        spec = Optional.ofNullable(adult)
                .map(PassengerSpecifications::isAdult)
                .map(spec::and)
                .orElse(spec);
        spec = Optional.ofNullable(gender)
                .map(PassengerSpecifications::hasGender)
                .map(spec::and)
                .orElse(spec);
        spec = Optional.ofNullable(noRelatives)
                .map(PassengerSpecifications::hasNoRelatives)
                .map(spec::and)
                .orElse(spec);

        // Используем стандартный метод findAll для выполнения спецификаций
        Page<PassengerEntity> passengerPage = passengerRepository.findAll(spec, pageable);
        List<PassengerEntityDto> dtoList = passengerPage.getContent().stream()
                .map(passengerMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, passengerPage.getTotalElements());
    }

    public PassengerStatistics getPassengerStatistics(List<PassengerEntityDto> passengers) {
        double totalFare = passengers.stream().mapToDouble(PassengerEntityDto::getFare).sum();
        int survivorsCount = (int) passengers.stream().filter(PassengerEntityDto::getSurvived).count();
        int relativesCount = (int) passengers.stream().filter(p ->
                Optional.ofNullable(p.getSiblingsSpousesAboard()).orElse(0) +
                        Optional.ofNullable(p.getParentsChildrenAboard()).orElse(0) > 0).count();

        return PassengerStatistics.builder()
                .totalFare(totalFare)
                .survivorsCount(survivorsCount)
                .relativesCount(relativesCount)
                .build();
    }

    public List<PassengerEntityDto> searchPassengersByName(String name) {
        return passengerRepository.findByNameContainingIgnoreCase(name, Pageable.unpaged())
                .getContent().stream()
                .map(passengerMapper::toDto)
                .collect(Collectors.toList());
    }
}