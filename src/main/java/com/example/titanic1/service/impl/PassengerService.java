package com.example.titanic1.service.impl;

import com.example.titanic1.dto.PassengerEntityDto;
import com.example.titanic1.dto.PassengerStatistics;
import com.example.titanic1.model.entity.PassengerEntity;
import com.example.titanic1.repo.PassengerRepository;
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
    @Autowired
    private PassengerRepository passengerRepository;

    @Cacheable(value = "passengers", sync = true)
    public Page<PassengerEntityDto> getFilteredAndSortedPassengers(
            Pageable pageable,
            String searchName,
            Boolean survived,
            Boolean adult,
            String gender,
            Boolean noRelatives
    ) {
        Specification<PassengerEntity> spec = Specification.where(null);

        if (searchName != null && !searchName.isEmpty()) {
            spec = spec.and(PassengerSpecifications.nameContains(searchName));
        }
        if (survived != null) {
            spec = spec.and(PassengerSpecifications.isSurvived(survived));
        }
        if (adult != null) {
            spec = spec.and(PassengerSpecifications.isAdult(adult));
        }
        if (gender != null && !gender.isEmpty()) {
            spec = spec.and(PassengerSpecifications.hasGender(gender));
        }
        if (noRelatives != null) {
            spec = spec.and(PassengerSpecifications.hasNoRelatives(noRelatives));
        }

        Page<PassengerEntity> passengerPage = passengerRepository.findAll(spec, pageable);
        List<PassengerEntityDto> dtoList = passengerPage.getContent().stream().map(this::convertToDto).collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, passengerPage.getTotalElements());
    }

    public List<PassengerEntityDto> searchPassengersByName(String name) {
        return passengerRepository.findByNameContainingIgnoreCase(name, Pageable.unpaged())
                .getContent().stream().map(this::convertToDto).collect(Collectors.toList());
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

    private PassengerEntityDto convertToDto(PassengerEntity passengerEntity) {
        return PassengerEntityDto.builder()
                .id(passengerEntity.getId())
                .name(passengerEntity.getName())
                .pClass(passengerEntity.getPClass())
                .survived(passengerEntity.getSurvived())
                .gender(passengerEntity.getGender())
                .age(passengerEntity.getAge())
                .siblingsSpousesAboard(passengerEntity.getSiblingsSpousesAboard())
                .parentsChildrenAboard(passengerEntity.getParentsChildrenAboard())
                .fare(passengerEntity.getFare())
                .build();
    }
}
