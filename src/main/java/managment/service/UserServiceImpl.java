package managment.service;

import com.sitech.users.*;
import io.quarkus.grpc.GrpcClient;
import managment.dto.user.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UserServiceImpl {

    @GrpcClient
    UserServiceGrpc.UserServiceBlockingStub userService;

    public updateUserPasswordResponseDTO updateUserPassword(updateUserPasswordDTO request){

        updateUserPasswordResponseDTO jsonResponse = new updateUserPasswordResponseDTO();

        StatusReplay kcResponse =
                userService.updateUserPassword(UpdateUserPasswordRequest.newBuilder()
                        .setRealmName(request.getRealmName())
                        .setUserId(request.getUserId())
                        .setPassword(request.getPassword())
                        .build());

        jsonResponse.setStatusCode(kcResponse.getStatusCode());

        return jsonResponse;

    }

    public AddUserResponseDTO updateUser(updateUserDTO request){

        AddUserResponseDTO jsonResponse = new AddUserResponseDTO();

        UserResponse kcResponse =
                userService.updateUser(UpdateUserRequest.newBuilder()
                        .setRealmName(request.getRealmName())
                        .setUserId(request.getUserId())
                        .setUserName(request.getUserName())
                        .setFirstName(request.getFirstName())
                        .setLastName(request.getLastName())
                        .setEmail(request.getEmail())
                        .setEnabled(request.getEnabled())
                        .build());

        jsonResponse.setId(kcResponse.getUserDto().getId());
        jsonResponse.setCreatedTimestamp(kcResponse.getUserDto().getCreatedTimestamp());
        jsonResponse.setUsername(kcResponse.getUserDto().getUsername());
        jsonResponse.setEnabled(kcResponse.getUserDto().getEnabled());
        jsonResponse.setFirstName(kcResponse.getUserDto().getFirstName());
        jsonResponse.setLastName(kcResponse.getUserDto().getLastName());
        jsonResponse.setEmail(kcResponse.getUserDto().getEmail());

        return jsonResponse;
    }

    public AddUserResponseDTO addUser(AddUserRequestDTO request) {

        AddUserResponseDTO jsonResponse = new AddUserResponseDTO();

        UserResponse kcResponse =
                userService.addUser(
                        AddUserRequest.newBuilder()
                                .setUserName(request.getUserName())
                                .setPass(request.getPass())
                                .setFirstName(request.getFirstName())
                                .setLastName(request.getLastName())
                                .setEmail(request.getEmail())
                                .setRealmName(request.getRealmName())
                                .setRealmRole(request.getRealmRole())
                                .build());

        jsonResponse.setId(kcResponse.getUserDto().getId());
        jsonResponse.setCreatedTimestamp(kcResponse.getUserDto().getCreatedTimestamp());
        jsonResponse.setUsername(kcResponse.getUserDto().getUsername());
        jsonResponse.setEnabled(kcResponse.getUserDto().getEnabled());
        jsonResponse.setFirstName(kcResponse.getUserDto().getFirstName());
        jsonResponse.setLastName(kcResponse.getUserDto().getLastName());
        jsonResponse.setEmail(kcResponse.getUserDto().getEmail());

        return jsonResponse;
    }

    public GetUserByUserNameResponseDTO getUserByUserName(String realmName, String userName) {

        GetUserByUserNameResponseDTO jsonResponse = new GetUserByUserNameResponseDTO();

        UserResponse kcResponse =
                userService.getUserByUserName(
                        GetUserByUserNameRequest.newBuilder()
                                .setRealmName(realmName)
                                .setUserName(userName)
                                .build());

        jsonResponse.setId(kcResponse.getUserDto().getId());
        jsonResponse.setCreatedTimestamp(kcResponse.getUserDto().getCreatedTimestamp());
        jsonResponse.setUsername(kcResponse.getUserDto().getUsername());
        jsonResponse.setEnabled(kcResponse.getUserDto().getEnabled());
        jsonResponse.setFirstName(kcResponse.getUserDto().getFirstName());
        jsonResponse.setLastName(kcResponse.getUserDto().getLastName());
        jsonResponse.setEmail(kcResponse.getUserDto().getEmail());

        return jsonResponse;
    }

    public List<GetUserGroupsResponseDTO> getUserGroups(String realmName, String userId) {

        List<GetUserGroupsResponseDTO> jsonResponse = new ArrayList<>();

        GetUserGroupsResponse KcResponse =
                userService.getUserGroups(
                        GetUserGroupRequest.newBuilder().setRealmName(realmName).setUserId(userId).build());

        for (int i = 0; i < KcResponse.getGroupDtoList().size(); i++) {
            GetUserGroupsResponseDTO dto = new GetUserGroupsResponseDTO();
            dto.setId(KcResponse.getGroupDto(i).getId());
            dto.setName(KcResponse.getGroupDto(i).getName());
            jsonResponse.add(dto);
        }

        return jsonResponse;
    }

    public List<GetUserRoleEffectiveResponseDTO> getUserRoleEffective(
            String realmName, String userId) {

        List<GetUserRoleEffectiveResponseDTO> jsonResponse = new ArrayList<>();

        GetUserRoleResponse KcResponse =
                userService.getUserRoleEffective(
                        UserRoleRequest.newBuilder().setRealmName(realmName).setUserId(userId).build());

        for (int i = 0; i < KcResponse.getRoleDtoList().size(); i++) {
            GetUserRoleEffectiveResponseDTO dto = new GetUserRoleEffectiveResponseDTO();
            dto.setId(KcResponse.getRoleDto(i).getId());
            dto.setDescription(KcResponse.getRoleDto(i).getDescription());
            dto.setName(KcResponse.getRoleDto(i).getName());
            jsonResponse.add(dto);
        }
        return jsonResponse;
    }

    public List<getUserRoleAvailableResponseDTO> getUserRoleAvailable(
            String realmName, String userId) {

        List<getUserRoleAvailableResponseDTO> jsonResponse = new ArrayList<>();

        GetUserRoleResponse KcResponse =
                userService.getUserRoleAvailable(
                        UserRoleRequest.newBuilder().setRealmName(realmName).setUserId(userId).build());

        for (int i = 0; i < KcResponse.getRoleDtoList().size(); i++) {
            getUserRoleAvailableResponseDTO dto = new getUserRoleAvailableResponseDTO();
            dto.setId(KcResponse.getRoleDto(i).getId());
            dto.setDescription(KcResponse.getRoleDto(i).getDescription());
            dto.setName(KcResponse.getRoleDto(i).getName());
            jsonResponse.add(dto);
        }
        return jsonResponse;
    }

    public List<findAllUsersInGroupResponseDTO> findAllUsersInGroup(String realmName, String groupName) {

        List<findAllUsersInGroupResponseDTO> jsonResponse = new ArrayList<>();

        UsersResponse KcResponse = userService.findAllUsersInGroup(
                UserGroupRequest.newBuilder().setRealmName(realmName).setGroupName(groupName).build());

        for (int i = 0; i < KcResponse.getUserDtoList().size(); i++) {
            findAllUsersInGroupResponseDTO dto = new findAllUsersInGroupResponseDTO();
            dto.setUsername(KcResponse.getUserDto(i).getUsername());
            dto.setFirstName(KcResponse.getUserDto(i).getFirstName());
            dto.setLastName(KcResponse.getUserDto(i).getLastName());
            dto.setEmail(KcResponse.getUserDto(i).getEmail());
            dto.setEnabled(KcResponse.getUserDto(i).getEnabled());
            dto.setId(KcResponse.getUserDto(i).getId());
            dto.setCreatedTimestamp(KcResponse.getUserDto(i).getCreatedTimestamp());
            jsonResponse.add(dto);

        }
        return jsonResponse;
    }
}
