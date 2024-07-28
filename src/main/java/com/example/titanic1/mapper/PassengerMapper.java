package com.example.titanic1.mapper;

import com.example.titanic1.dto.PassengerEntityDto;
import com.example.titanic1.model.entity.PassengerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PassengerMapper {
    PassengerMapper INSTANCE = Mappers.getMapper(PassengerMapper.class);

    PassengerEntityDto toDto(PassengerEntity passenger);
    PassengerEntity toEntity(PassengerEntityDto dto);
}