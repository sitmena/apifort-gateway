package me.sitech.integration.mapper;

import com.sitech.dto.Dto;
import me.sitech.integration.domain.response.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "cdi",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);
    RoleResponse toDto(Dto.RoleDto roleDto);
    List<RoleResponse> toDtoList(List<Dto.RoleDto> roleDto);

}
