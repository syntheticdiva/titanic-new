package com.example.titanic1.service.impl;

import com.example.titanic1.model.entity.PassengerEntity;
import com.example.titanic1.model.enums.Gender;
import com.example.titanic1.model.enums.PClass;
import com.example.titanic1.repo.PassengerRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class CsvImportService {

    @Autowired
    private PassengerRepository couchbasePassengerRepository;

    @PostConstruct
    public void importCsv() {
        String csvFile = "titanic.csv"; // Путь к вашему CSV-файлу
        System.out.println("Starting CSV import");

        try {
            Resource resource = new ClassPathResource(csvFile);
            if (!resource.exists()) {
                throw new IllegalArgumentException("File not found: " + csvFile);
            }

            try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
                String[] nextLine;
                reader.readNext(); // Пропускаем заголовок

                while ((nextLine = reader.readNext()) != null) {
                    PassengerEntity passenger = new PassengerEntity();
                    passenger.setSurvived("1".equals(nextLine[0]));
                    passenger.setPClass(PClass.fromInt(Integer.parseInt(nextLine[1])));
                    passenger.setName(nextLine[2]);
                    passenger.setGender(Gender.valueOf(nextLine[3]));
                    passenger.setAge(nextLine[4].isEmpty() ? null : Double.parseDouble(nextLine[4]));
                    passenger.setSiblingsSpousesAboard(Integer.parseInt(nextLine[5]));
                    passenger.setParentsChildrenAboard(Integer.parseInt(nextLine[6]));
                    passenger.setFare(Double.parseDouble(nextLine[7].replace(",", "."))); // Преобразование запятой в точку

                    couchbasePassengerRepository.save(passenger);
                }
                System.out.println("CSV import completed successfully");
            }
        } catch (CsvValidationException | IOException e) {
            e.printStackTrace(); // Обработка исключений
            System.out.println("CSV import failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Обработка общих исключений
            System.out.println("General exception: " + e.getMessage());
        }
    }
}