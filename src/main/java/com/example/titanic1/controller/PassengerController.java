package com.example.titanic1.controller;

import com.example.titanic1.dto.PassengerEntityDto;
import com.example.titanic1.dto.PassengerStatistics;
import com.example.titanic1.model.enums.Gender;
import com.example.titanic1.service.impl.PassengerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;


@Controller
@RequestMapping("/passengers")
public class PassengerController {

    private static final Logger logger = LoggerFactory.getLogger(PassengerController.class);
    private final PassengerService passengerService;

    @Autowired
    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping
    public ModelAndView getPassengers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String searchName,
            @RequestParam(required = false) Boolean survived,
            @RequestParam(required = false) Boolean adult,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) Boolean noRelatives
    ) {
        logger.info("Parameters: page={}, size={}, sortField={}, sortDirection={}, searchName={}, survived={}, adult={}, gender={}, noRelatives={}",
                page, size, sortField, sortDirection, searchName, survived, adult, gender, noRelatives);

        // Определение сортировки
        String sortBy = (sortField != null && !sortField.isEmpty()) ? sortField : "name";
        Sort.Direction direction = (sortDirection != null && !sortDirection.isEmpty()) ?
                Sort.Direction.fromString(sortDirection) : Sort.Direction.ASC;

        // Создание объекта Pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Получение отфильтрованных и отсортированных пассажиров
        Page<PassengerEntityDto> passengerPage = passengerService.getFilteredAndSortedPassengers(
                pageable, searchName, survived, adult, gender, noRelatives
        );

        // Расчет статистики, передавая список пассажиров
        PassengerStatistics statistics = passengerService.getPassengerStatistics(passengerPage.getContent());

        // Создание ModelAndView и добавление атрибутов
        ModelAndView modelAndView = new ModelAndView("passengers");
        modelAndView.addObject("passengers", passengerPage.getContent());
        modelAndView.addObject("currentPage", passengerPage.getNumber());
        modelAndView.addObject("totalItems", passengerPage.getTotalElements());
        modelAndView.addObject("totalPages", passengerPage.getTotalPages());
        modelAndView.addObject("statistics", statistics);
        modelAndView.addObject("searchName", searchName);
        modelAndView.addObject("survived", survived);
        modelAndView.addObject("adult", adult);
        modelAndView.addObject("gender", gender);
        modelAndView.addObject("noRelatives", noRelatives);

        return modelAndView;
    }
}
//@Controller
//@RequestMapping("/passengers")
//public class PassengerController {
//
//    private static final Logger logger = LoggerFactory.getLogger(PassengerController.class);
//    private final PassengerService passengerService;
//
//    @Autowired
//    public PassengerController(PassengerService passengerService) {
//        this.passengerService = passengerService;
//    }
//
//    @GetMapping
//    public ModelAndView getPassengers(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "50") int size,
//            @RequestParam(required = false) String sortField,
//            @RequestParam(required = false) String sortDirection,
//            @RequestParam(required = false) String searchName,
//            @RequestParam(required = false) Boolean survived,
//            @RequestParam(required = false) Boolean adult,
//            @RequestParam(required = false) Gender gender,
//            @RequestParam(required = false) Boolean noRelatives
//    ) {
//        searchName = (searchName != null) ? searchName.trim() : null;
//
//        logger.info("Received parameters: searchName={}", searchName);
//
//        logger.info("Parameters: page={}, size={}, sortField={}, sortDirection={}, searchName={}, survived={}, adult={}, gender={}, noRelatives={}",
//                page, size, sortField, sortDirection, searchName, survived, adult, gender, noRelatives);
//
//        // Определение сортировки
//        String sortBy = (sortField != null && !sortField.isEmpty()) ? sortField : "name";
//        Sort.Direction direction = (sortDirection != null && !sortDirection.isEmpty()) ? Sort.Direction.fromString(sortDirection) : Sort.Direction.ASC;
//
//        // Создание объекта Pageable
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
//
//        // Получение отфильтрованных и отсортированных пассажиров
//        Page<PassengerEntityDto> passengerPage = passengerService.getFilteredAndSortedPassengers(
//                pageable, searchName, survived, adult, gender, noRelatives
//        );
//
//        // Расчет статистики
//        PassengerStatistics statistics = passengerService.getPassengerStatistics(passengerPage.getContent());
//
//        // Создание ModelAndView и добавление атрибутов
//        ModelAndView modelAndView = new ModelAndView("passengers");
//        modelAndView.addObject("passengers", passengerPage.getContent());
//        modelAndView.addObject("currentPage", passengerPage.getNumber());
//        modelAndView.addObject("totalItems", passengerPage.getTotalElements());
//        modelAndView.addObject("totalPages", passengerPage.getTotalPages());
//        modelAndView.addObject("statistics", statistics);
//
//        return modelAndView;
//    }
//
//    @GetMapping("/search")
//    public ModelAndView searchPassenger(@RequestParam String name) {
//        List<PassengerEntityDto> passengers = passengerService.searchPassengersByName(name);
//
//        PassengerStatistics statistics = passengerService.getPassengerStatistics(passengers);
//
//        ModelAndView modelAndView = new ModelAndView("passengers");
//        modelAndView.addObject("passengers", passengers);
//        modelAndView.addObject("statistics", statistics);
//
//        return modelAndView;
//    }
//}
