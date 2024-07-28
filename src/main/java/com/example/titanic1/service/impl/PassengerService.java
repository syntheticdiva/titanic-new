package com.example.titanic1.service.impl;

import com.example.titanic1.dto.PassengerEntityDto;
import com.example.titanic1.dto.PassengerStatistics;
import com.example.titanic1.mapper.PassengerMapper;
import com.example.titanic1.model.entity.PassengerEntity;
import com.example.titanic1.model.enums.Gender;
import com.example.titanic1.repo.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private PassengerMapper passengerMapper;

    public Page<PassengerEntityDto> getFilteredAndSortedPassengers(
            Pageable pageable,
            String searchName,
            Boolean survived,
            Boolean adult,
            Gender gender,
            Boolean noRelatives,
            String sortField,
            boolean ascending
    ) {
        // Получаем всех пассажиров
        List<PassengerEntity> passengers = passengerRepository.findAll();

        // Фильтрация по параметрам
        if (adult != null && adult) {
            passengers = passengers.stream()
                    .filter(passenger -> passenger.getAge() != null && passenger.getAge() > 16)
                    .collect(Collectors.toList());
        }

        // Фильтрация по имени
        if (searchName != null && !searchName.isEmpty()) {
            passengers = passengers.stream()
                    .filter(passenger -> passenger.getName().toLowerCase().contains(searchName.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Фильтрация по выжившим
        if (survived != null) {
            passengers = passengers.stream()
                    .filter(passenger -> passenger.getSurvived().equals(survived))
                    .collect(Collectors.toList());
        }

        // Фильтрация по полу
        if (gender != null) {
            passengers = passengers.stream()
                    .filter(passenger -> passenger.getGender().equals(gender))
                    .collect(Collectors.toList());
        }

        // Фильтрация по наличию родственников
        if (noRelatives != null) {
            passengers = passengers.stream()
                    .filter(passenger -> (noRelatives ?
                            (passenger.getSiblingsSpousesAboard() == 0 && passenger.getParentsChildrenAboard() == 0) :
                            (passenger.getSiblingsSpousesAboard() > 0 || passenger.getParentsChildrenAboard() > 0)))
                    .collect(Collectors.toList());
        }

                    // Сортировка
            if (sortField != null) {
                switch (sortField.toLowerCase()) {
                    case "имя":
                        passengers.sort(Comparator.comparing(PassengerEntity::getName, ascending ? Comparator.naturalOrder() : Comparator.reverseOrder()));
                        break;
                    case "возраст":
                        passengers.sort(Comparator.comparing(PassengerEntity::getAge, ascending ? Comparator.naturalOrder() : Comparator.reverseOrder()));
                        break;
                    case "оплата":
                        passengers.sort(Comparator.comparing(PassengerEntity::getFare, ascending ? Comparator.naturalOrder() : Comparator.reverseOrder()));
                        break;
                    default:
                        // Если поле сортировки не распознано, можно оставить как есть или выбросить исключение
                        break;
                }
            }

        // Пагинация
        int total = passengers.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        List<PassengerEntityDto> pagedPassengers = passengers.subList(start, end).stream()
                .map(passengerMapper::toDto) // Преобразование в DTO
                .collect(Collectors.toList());

        return new PageImpl<>(pagedPassengers, pageable, total);
    }

    @Cacheable("passengerStatistics")
    public PassengerStatistics getPassengerStatistics(List<PassengerEntityDto> passengers) {
        if (passengers == null || passengers.isEmpty()) {
            return new PassengerStatistics(0, 0, 0);
        }

        double totalFare = passengers.stream().mapToDouble(PassengerEntityDto::getFare).sum();
        int relativesCount = (int) passengers.stream().filter(p -> p.getSiblingsSpousesAboard() > 0 || p.getParentsChildrenAboard() > 0).count();
        int survivorsCount = (int) passengers.stream().filter(PassengerEntityDto::getSurvived).count();

        return new PassengerStatistics(totalFare, survivorsCount, relativesCount);
    }

    public List<PassengerEntityDto> searchPassengersByName(String name) {
        List<PassengerEntity> passengers = passengerRepository.findByNameContainingIgnoreCase(name);
        return passengers.stream()
                .map(passengerMapper::toDto)
                .collect(Collectors.toList());
    }
}

//@Service
//public class PassengerService {
//
//    @Autowired
//    private PassengerRepository passengerRepository;
//
//        public Page<PassengerEntityDto> getFilteredAndSortedPassengers(
//                Pageable pageable,
//                String searchName,
//                Boolean survived,
//                Boolean adult,
//                Gender gender,
//                Boolean noRelatives,
//                String sortField, // Поле для сортировки
//                boolean ascending // Направление сортировки
//        ) {
//            // Получаем всех пассажиров
//            List<PassengerEntity> passengers = passengerRepository.findAll();
//
//            // Фильтрация по параметрам
//            if (adult != null && adult) {
//                // Фильтруем только совершеннолетних пассажиров (старше 16 лет)
//                passengers = passengers.stream()
//                        .filter(passenger -> passenger.getAge() != null && passenger.getAge() > 16)
//                        .collect(Collectors.toList());
//            }
//
//            // Фильтрация по имени
//            if (searchName != null && !searchName.isEmpty()) {
//                passengers = passengers.stream()
//                        .filter(passenger -> passenger.getName().toLowerCase().contains(searchName.toLowerCase()))
//                        .collect(Collectors.toList());
//            }
//
//            // Фильтрация по выжившим
//            if (survived != null) {
//                passengers = passengers.stream()
//                        .filter(passenger -> passenger.getSurvived().equals(survived))
//                        .collect(Collectors.toList());
//            }
//
//            // Фильтрация по полу
//            if (gender != null) {
//                passengers = passengers.stream()
//                        .filter(passenger -> passenger.getGender().equals(gender))
//                        .collect(Collectors.toList());
//            }
//
//            // Фильтрация по наличию родственников
//            if (noRelatives != null) {
//                passengers = passengers.stream()
//                        .filter(passenger -> (noRelatives ?
//                                (passenger.getSiblingsSpousesAboard() == 0 && passenger.getParentsChildrenAboard() == 0) :
//                                (passenger.getSiblingsSpousesAboard() > 0 || passenger.getParentsChildrenAboard() > 0)))
//                        .collect(Collectors.toList());
//            }
//
//            // Сортировка
//            if (sortField != null) {
//                switch (sortField.toLowerCase()) {
//                    case "имя":
//                        passengers.sort(Comparator.comparing(PassengerEntity::getName, ascending ? Comparator.naturalOrder() : Comparator.reverseOrder()));
//                        break;
//                    case "возраст":
//                        passengers.sort(Comparator.comparing(PassengerEntity::getAge, ascending ? Comparator.naturalOrder() : Comparator.reverseOrder()));
//                        break;
//                    case "оплата":
//                        passengers.sort(Comparator.comparing(PassengerEntity::getFare, ascending ? Comparator.naturalOrder() : Comparator.reverseOrder()));
//                        break;
//                    default:
//                        // Если поле сортировки не распознано, можно оставить как есть или выбросить исключение
//                        break;
//                }
//            }
//
//            // Пагинация
//            int total = passengers.size();
//            int start = (int) pageable.getOffset();
//            int end = Math.min(start + pageable.getPageSize(), total);
//            List<PassengerEntityDto> pagedPassengers = passengers.subList(start, end).stream()
//                    .map(this::convertToDto) // Преобразование в DTO
//                    .collect(Collectors.toList());
//
//            return new PageImpl<>(pagedPassengers, pageable, total);
//        }
//
//        @Cacheable("passengerStatistics")
//        public PassengerStatistics getPassengerStatistics(List<PassengerEntityDto> passengers) {
//            double totalFare = passengers.stream().mapToDouble(PassengerEntityDto::getFare).sum();
//            int relativesCount = (int) passengers.stream().filter(p -> p.getSiblingsSpousesAboard() > 0 || p.getParentsChildrenAboard() > 0).count();
//            int survivorsCount = (int) passengers.stream().filter(PassengerEntityDto::getSurvived).count();
//
//            return new PassengerStatistics(totalFare, survivorsCount, relativesCount);
//        }
//
//        private PassengerEntityDto convertToDto(PassengerEntity passenger) {
//            return new PassengerEntityDto(
//                    passenger.getId(),
//                    passenger.getName(),
//                    passenger.getPClass(),
//                    passenger.getSurvived(),
//                    passenger.getGender(),
//                    passenger.getAge(),
//                    passenger.getSiblingsSpousesAboard(),
//                    passenger.getParentsChildrenAboard(),
//                    passenger.getFare()
//            );
//        }
//
//        public List<PassengerEntityDto> searchPassengersByName(String name) {
//            List<PassengerEntity> passengers = passengerRepository.findByNameContainingIgnoreCase(name);
//            return passengers.stream().map(this::convertToDto).collect(Collectors.toList());
//        }
//    }
//
