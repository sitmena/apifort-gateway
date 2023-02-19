package me.sitech.integration.mapper;

import me.sitech.integration.domain.module.Dto;
import me.sitech.integration.domain.response.GroupResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StatusMapper {

    StatusMapper INSTANCE = Mappers.getMapper(StatusMapper.class);
    GroupResponse toDto(Dto.GroupDto groupDto);
}
