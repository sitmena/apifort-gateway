package me.sitech.integration.mapper;

import com.sitech.dto.Dto;
import me.sitech.integration.domain.response.realm.RealmResponse;
import me.sitech.integration.domain.response.realm.RealmGroupResponse;
import me.sitech.integration.domain.response.ProfileUserResponse;
import me.sitech.integration.domain.response.realm.RealmClientResponse;
import me.sitech.integration.domain.response.realm.RealmRoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "cdi",  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RealmMapper {

    RealmMapper INSTANCE = Mappers.getMapper(RealmMapper.class);
    RealmResponse toDto(Dto.RealmDto realmDto);
    List<RealmResponse> toDtoList(List<Dto.RealmDto> realmDto);
    List<RealmGroupResponse> toGroupDtoList(List<Dto.GroupDto> groupDto);
    List<ProfileUserResponse> toUserDtoList(List<Dto.UserDto> userDto);
    List<RealmClientResponse> toClientDto(List<Dto.ClientDto> clientDto);
    List<RealmRoleResponse> toRoleDto(List<Dto.RoleDto> roleDto);
}
