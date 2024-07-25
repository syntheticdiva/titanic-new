package com.example.titanic1.controller;

import com.example.titanic1.dto.PassengerEntityDto;
import com.example.titanic1.dto.PassengerStatistics;
import com.example.titanic1.service.impl.PassengerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
@Controller
@RequestMapping("/passengers")
public class PassengerController {
    private final PassengerService passengerService;
    private final CacheManager cacheManager;

    @Autowired
    public PassengerController(PassengerService passengerService, CacheManager cacheManager) {
        this.passengerService = passengerService;
        this.cacheManager = cacheManager;
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
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Boolean noRelatives
    ) {
        String sortBy = sortField != null ? sortField : "name";
        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<PassengerEntityDto> passengerPage = passengerService.getFilteredAndSortedPassengers(
                pageable, searchName, survived, adult, gender, noRelatives
        );

        PassengerStatistics statistics = passengerService.getPassengerStatistics(passengerPage.getContent());

        ModelAndView modelAndView = new ModelAndView("passengers");
        modelAndView.addObject("passengers", passengerPage.getContent());
        modelAndView.addObject("currentPage", passengerPage.getNumber());
        modelAndView.addObject("totalItems", passengerPage.getTotalElements());
        modelAndView.addObject("totalPages", passengerPage.getTotalPages());
        modelAndView.addObject("statistics", statistics);

        return modelAndView;
    }

    @GetMapping("/search")
    public ModelAndView searchPassenger(@RequestParam String name) {
        List<PassengerEntityDto> passengers = passengerService.searchPassengersByName(name);
        ModelAndView modelAndView = new ModelAndView("passengers");
        modelAndView.addObject("passengers", passengers);PassengerStatistics statistics = passengerService.getPassengerStatistics(passengers);
        modelAndView.addObject("statistics", statistics);
        return modelAndView;
    }
}