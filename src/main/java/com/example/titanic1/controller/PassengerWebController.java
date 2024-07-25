package com.example.titanic1.controller;

import com.example.titanic1.dto.PassengerEntityDto;
import com.example.titanic1.dto.PassengerStatistics;
import com.example.titanic1.model.entity.PassengerEntity;

import com.example.titanic1.service.impl.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
@Controller
@RequestMapping("/passengers-web")
public class PassengerWebController {

    private final PassengerService passengerService;

    @Autowired
    public PassengerWebController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping
    public String getPassengersPage(Model model, @PageableDefault(size = 20, sort = "name") Pageable pageable,
                                    @RequestParam(required = false) String searchName,
                                    @RequestParam(required = false) Boolean survived,
                                    @RequestParam(required = false) Boolean adult,
                                    @RequestParam(required = false) String gender,
                                    @RequestParam(required = false) Boolean noRelatives) {
        Page<PassengerEntityDto> passengerPage = passengerService.getFilteredAndSortedPassengers(
                pageable, searchName, survived, adult, gender, noRelatives
        );
        List<PassengerEntityDto> filteredPassengers = passengerPage.getContent();

        // Calculate statistics
        PassengerStatistics statistics = passengerService.getPassengerStatistics(filteredPassengers);

        model.addAttribute("passengers", filteredPassengers);
        model.addAttribute("statistics", statistics);
        model.addAttribute("page", passengerPage);
        model.addAttribute("searchName", searchName);
        model.addAttribute("survived", survived);
        model.addAttribute("adult", adult);
        model.addAttribute("gender", gender);
        model.addAttribute("noRelatives", noRelatives);

        return "passengers";
    }

    // Convert DTO to Entity (if necessary, you may remove this part if not used)
    private PassengerEntity convertToEntity(PassengerEntityDto dto) {
        return PassengerEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .pClass(dto.getPClass())
                .survived(dto.getSurvived())
                .gender(dto.getGender())
                .age(dto.getAge())
                .siblingsSpousesAboard(dto.getSiblingsSpousesAboard())
                .parentsChildrenAboard(dto.getParentsChildrenAboard())
                .fare(dto.getFare())
                .build();
    }
}