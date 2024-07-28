package com.example.titanic1.mapper;

import com.example.titanic1.dto.PassengerEntityDto;
import com.example.titanic1.model.entity.PassengerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

    PassengerEntityDto toDto(PassengerEntity passenger);
    PassengerEntity toEntity(PassengerEntityDto dto);


}