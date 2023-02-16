package managment.service;

import com.google.protobuf.Empty;
import com.sitech.dto.Dto;
import com.sitech.realm.*;
import io.quarkus.grpc.GrpcClient;
import managment.dto.realm.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class RealmServiceImpl {

    @GrpcClient
    RealmServiceGrpc.RealmServiceBlockingStub realmService;

    public AddRealmResponseDTO addRealm(String realmName) {

        RealmResponse kcResponse =
                realmService.addRealm(AddRealmRequest.newBuilder().setRealmName(realmName).build());

        return new AddRealmResponseDTO(kcResponse.getRealmDto().getId(),
                kcResponse.getRealmDto().getRealm(),
                kcResponse.getRealmDto().getEnabled());
    }

    public List<GetRealmsResponseDTO> getRealms() {

        List<GetRealmsResponseDTO> jsonResponse = new ArrayList<>();

        RealmsResponse kcResponse = realmService.getRealms(Empty.newBuilder().build());

        for (Dto.RealmDto realmDto : kcResponse.getRealmDtoList()) {
            jsonResponse.add(new GetRealmsResponseDTO(realmDto.getId(), realmDto.getRealm(), realmDto.getEnabled()));
        }

        return jsonResponse;
    }

    public AddRealmGroupResponseDTO addRealmGroup(AddRealmGroupRequestDTO request) {

        AddRealmGroupResponseDTO jsonResponse = new AddRealmGroupResponseDTO();

        AddRealmGroupResponse kcResponse =
                realmService.addRealmGroup(
                        AddRealmGroupRequest.newBuilder()
                                .setRealmName(request.getRealmName())
                                .setGroupName(request.getGroupName())
                                .build());

        jsonResponse.setStatus(kcResponse.getStatus());

        return jsonResponse;
    }

    public GetRealmByNameResponseDTO getRealmByName(String realmName) {

        GetRealmByNameResponseDTO jsonResponse = new GetRealmByNameResponseDTO();

        RealmResponse kcResponse =
                realmService.getRealmByName(RealmNameRequest.newBuilder().setRealmName(realmName).build());

        jsonResponse.setId(kcResponse.getRealmDto().getId());
        jsonResponse.setRealm(kcResponse.getRealmDto().getRealm());
        jsonResponse.setDisplayName(kcResponse.getRealmDto().getDisplayName());
        jsonResponse.setEnabled(kcResponse.getRealmDto().getEnabled());

        return jsonResponse;
    }

    public List<GetRealmUsersResponseDTO> getRealmUsers(String realmName) {

        List<GetRealmUsersResponseDTO> jsonResponse = new ArrayList<>();

        RealmUserResponse kcResponse =
                realmService.getRealmUsers(RealmNameRequest.newBuilder().setRealmName(realmName).build());

        for (Dto.UserDto userDto : kcResponse.getUserDtoList()) {
            jsonResponse.add(new GetRealmUsersResponseDTO(userDto.getId(),
                    userDto.getCreatedTimestamp(),
                    userDto.getUsername(),
                    userDto.getEnabled(),
                    userDto.getFirstName(),
                    userDto.getLastName(),
                    userDto.getEmail()));
        }

        return jsonResponse;
    }

    public List<GetRealmGroupsResponseDTO> getRealmGroups(String realmName) {

        List<GetRealmGroupsResponseDTO> jsonResponse = new ArrayList<>();

        GetRealmGroupsResponse kcResponse =
                realmService.getRealmGroups(RealmNameRequest.newBuilder().setRealmName(realmName).build());

        for (Dto.GroupDto groupDto : kcResponse.getGroupDtoList()) {
            jsonResponse.add(new GetRealmGroupsResponseDTO(groupDto.getId(), groupDto.getName()));
        }

        return jsonResponse;

    }

    public LogoutAllUsersResponse logoutAllUsers(String realmName){

        LogoutAllUsersResponse jsonResponse = new LogoutAllUsersResponse();

        StatusResponse kcResponse = realmService.logoutAllUsers(RealmNameRequest.newBuilder().setRealmName(realmName).build());
        jsonResponse.setCode(kcResponse.getStatus());

        return jsonResponse;

    }
}
