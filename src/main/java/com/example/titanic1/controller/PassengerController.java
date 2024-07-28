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
    public String getPassengers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @ModelAttribute("sortField") String sortField,
            @ModelAttribute("sortDirection") String sortDirection,
            @RequestParam(required = false) String searchName,
            @RequestParam(required = false) Boolean survived,
            @RequestParam(required = false) Boolean adult,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) Boolean noRelatives,
            Model model
    ) {
        logger.info("Parameters: page={}, size={}, sortField={}, sortDirection={}, searchName={}, survived={}, adult={}, gender={}, noRelatives={}",
                page, size, sortField, sortDirection, searchName, survived, adult, gender, noRelatives);

        // Определение сортировки
        String sortBy = (sortField != null && !sortField.isEmpty()) ? sortField : "name";
        Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Создание объекта Pageable с сортировкой
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        try {
            // Получение отфильтрованных и отсортированных пассажиров
            Page<PassengerEntityDto> passengerPage = passengerService.getFilteredAndSortedPassengers(
                    pageable, searchName, survived, adult, gender, noRelatives, sortBy, direction == Sort.Direction.ASC
            );

            // Расчет статистики, передавая список пассажиров
            PassengerStatistics statistics = passengerService.getPassengerStatistics(passengerPage.getContent());

            // Добавление атрибутов в модель
            model.addAttribute("passengers", passengerPage.getContent());
            model.addAttribute("currentPage", passengerPage.getNumber());
            model.addAttribute("totalItems", passengerPage.getTotalElements());
            model.addAttribute("totalPages", passengerPage.getTotalPages());
            model.addAttribute("statistics", statistics);
            model.addAttribute("searchName", searchName);
            model.addAttribute("survived", survived);
            model.addAttribute("adult", adult);
            model.addAttribute("gender", gender);
            model.addAttribute("noRelatives", noRelatives);

            return "passengers";

        } catch (Exception e) {
            logger.error("Error while fetching passengers", e);
            // Обработка ошибки, возможно, перенаправление на страницу ошибки
            return "error";
        }
    }
}