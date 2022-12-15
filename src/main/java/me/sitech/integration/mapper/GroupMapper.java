package me.sitech.integration.mapper;

import com.sitech.dto.Dto;
import me.sitech.integration.domain.response.GroupResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "cdi",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GroupMapper {

    GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);
    GroupResponse toDto(Dto.GroupDto groupDto);
    List<GroupResponse> toDtoList(List<Dto.GroupDto> groupDto);
}
