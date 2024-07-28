package com.example.titanic1.dto;

import com.example.titanic1.model.enums.Gender;
import com.example.titanic1.model.enums.PClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PassengerEntityDto)) return false;
        PassengerEntityDto that = (PassengerEntityDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(fare, that.fare); // Сравнение по id, name и fare
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, fare); // Генерация hash-кода по id, name и fare
    }
}
