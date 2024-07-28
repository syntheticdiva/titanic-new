package com.example.titanic1.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class PassengerStatistics {
    private double totalFare;
    private int survivorsCount;
    private int relativesCount;

    // Публичный конструктор
    public PassengerStatistics(double totalFare, int survivorsCount, int relativesCount) {
        this.totalFare = totalFare;
        this.survivorsCount = survivorsCount;
        this.relativesCount = relativesCount;
    }
}