package me.sitech.integration.mapper;

import com.sitech.dto.Dto;
import me.sitech.integration.domain.response.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "cdi",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserResponse toDto(Dto.UserDto userDto);

    List<UserResponse> toDtoList(List<Dto.UserDto> userDto);
}
