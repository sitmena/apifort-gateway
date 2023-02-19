package me.sitech.integration.mapper;

import me.sitech.integration.domain.module.Dto;
import me.sitech.integration.domain.response.token.TokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TokenMapper {

    TokenMapper INSTANCE = Mappers.getMapper(TokenMapper.class);

    TokenResponse toDto(Dto.UserAccessTokenDto tokenDto);
}
