package com.example.titanic1.service.impl;
import com.example.titanic1.model.entity.PassengerEntity;
import com.example.titanic1.model.enums.Gender;
import com.example.titanic1.model.enums.PClass;
import com.example.titanic1.repo.PassengerRepository;
import com.opencsv.CSVReader;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStreamReader;



import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class CsvImportService {

    @Autowired
    private PassengerRepository passengerRepository;

    @PostConstruct
    public void importCsv() {
        String csvFile = "titanic.csv"; // Путь к вашему CSV-файлу
        System.out.println("Starting CSV import");

        try {
            Resource resource = new ClassPathResource(csvFile);
            validateResource(resource);

            try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
                String[] nextLine;
                reader.readNext(); // Пропускаем заголовок

                while ((nextLine = reader.readNext()) != null) {
                    PassengerEntity passenger = createPassengerFromCsvLine(nextLine);
                    passengerRepository.save(passenger);
                }
                System.out.println("CSV import completed successfully");
            }
        } catch (CsvValidationException e) {
            System.err.println("CSV validation failed: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("General exception: " + e.getMessage());
        }
    }

    private void validateResource(Resource resource) {
        if (!resource.exists()) {
            throw new IllegalArgumentException("File not found: " + resource.getFilename());
        }
    }

    private PassengerEntity createPassengerFromCsvLine(String[] line) {
        PassengerEntity passenger = new PassengerEntity();
        passenger.setSurvived("1".equals(line[0]));
        passenger.setPClass(PClass.fromInt(Integer.parseInt(line[1])));
        passenger.setName(line[2]);
        passenger.setGender(Gender.valueOf(line[3]));
        passenger.setAge(line[4].isEmpty() ? null : Double.parseDouble(line[4]));
        passenger.setSiblingsSpousesAboard(Integer.parseInt(line[5]));
        passenger.setParentsChildrenAboard(Integer.parseInt(line[6]));
        passenger.setFare(Double.parseDouble(line[7].replace(",", "."))); // Преобразование запятой в точку
        return passenger;
    }
}
