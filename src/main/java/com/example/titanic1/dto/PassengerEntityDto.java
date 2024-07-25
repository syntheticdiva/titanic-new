package com.example.titanic1.dto;

import com.example.titanic1.model.enums.Gender;
import com.example.titanic1.model.enums.PClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerEntityDto {
    private Long id;
    private String name;
    private PClass pClass;
    private Boolean survived;
    private Gender gender;
    private Double age;
    private Integer siblingsSpousesAboard;
    private Integer parentsChildrenAboard;
    private Double fare;
}
