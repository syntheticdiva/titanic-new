package com.example.titanic1.model.entity;

import com.example.titanic1.model.enums.Gender;
import com.example.titanic1.model.enums.PClass;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "passengers")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
public class PassengerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "p_class", nullable = false)
    private PClass pClass;

    @Column(name = "survived", nullable = false)
    private Boolean survived;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false)
    private Gender gender;

    @Column(name = "age")
    private Double age;

    @Column(name = "siblings_spouses")
    private Integer siblingsSpousesAboard;

    @Column(name = "parents_children")
    private Integer parentsChildrenAboard;

    @Column(name = "fare")
    private Double fare;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PassengerEntity passenger = (PassengerEntity) o;
        return Objects.equals(id, passenger.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
