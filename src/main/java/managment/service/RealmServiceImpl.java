package managment.service;

import com.google.protobuf.Empty;
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

        AddRealmResponseDTO jsonResponse = new AddRealmResponseDTO();

        RealmResponse kcResponse =
                realmService.addRealm(AddRealmRequest.newBuilder().setRealmName(realmName).build());

        jsonResponse.setId(kcResponse.getRealmDto().getId());
        jsonResponse.setRealm(kcResponse.getRealmDto().getRealm());
        jsonResponse.setEnabled(kcResponse.getRealmDto().getEnabled());

        return jsonResponse;
    }

    public List<GetRealmsResponseDTO> getRealms() {

        List<GetRealmsResponseDTO> jsonResponse = new ArrayList<>();

        RealmsResponse kcResponse = realmService.getRealms(Empty.newBuilder().build());

        for (int i = 0; i < kcResponse.getRealmDtoList().size(); i++) {
            GetRealmsResponseDTO dto = new GetRealmsResponseDTO();
            dto.setId(kcResponse.getRealmDto(i).getId());
            dto.setRealm(kcResponse.getRealmDto(i).getRealm());
            dto.setEnabled(kcResponse.getRealmDto(i).getEnabled());
            jsonResponse.add(dto);
        }

        return jsonResponse;
    }

    public AddRealmGroupResponseDTO AddRealmGroup(AddRealmGroupRequestDTO request) {

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

        for (int i = 0; i < kcResponse.getUserDtoList().size(); i++) {
            GetRealmUsersResponseDTO dto = new GetRealmUsersResponseDTO();
            dto.setId(kcResponse.getUserDto(i).getId());
            dto.setCreatedTimestamp(kcResponse.getUserDto(i).getCreatedTimestamp());
            dto.setUsername(kcResponse.getUserDto(i).getUsername());
            dto.setEnabled(kcResponse.getUserDto(i).getEnabled());
            dto.setFirstName(kcResponse.getUserDto(i).getFirstName());
            dto.setLastName(kcResponse.getUserDto(i).getLastName());
            dto.setEmail(kcResponse.getUserDto(i).getEmail());
            jsonResponse.add(dto);
        }

        return jsonResponse;
    }

    public List<GetRealmGroupsResponseDTO> getRealmGroups(String realmName) {

        List<GetRealmGroupsResponseDTO> jsonResponse = new ArrayList<>();

        GetRealmGroupsResponse kcResponse =
                realmService.getRealmGroups(RealmNameRequest.newBuilder().setRealmName(realmName).build());

        for (int i = 0; i < kcResponse.getGroupDtoList().size(); i++) {
            GetRealmGroupsResponseDTO dto = new GetRealmGroupsResponseDTO();
            dto.setId(kcResponse.getGroupDto(i).getId());
            dto.setName(kcResponse.getGroupDto(i).getName());
            jsonResponse.add(dto);
        }

        return jsonResponse;

    }

    public LogoutAllUsersResponse LogoutAllUsers(String realmName){

        LogoutAllUsersResponse jsonResponse = new LogoutAllUsersResponse();

        StatusResponse kcResponse = realmService.logoutAllUsers(RealmNameRequest.newBuilder().setRealmName(realmName).build());
        jsonResponse.setCode(kcResponse.getStatus());

        return jsonResponse;

    }
}
