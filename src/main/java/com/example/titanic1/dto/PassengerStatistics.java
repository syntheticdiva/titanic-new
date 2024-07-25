package com.example.titanic1.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PassengerStatistics {
    private double totalFare;
    private int survivorsCount;
    private int relativesCount;
}
