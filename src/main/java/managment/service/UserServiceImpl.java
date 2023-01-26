package managment.service;

import com.sitech.users.Credentials;
import com.sitech.users.*;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.security.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import managment.dto.user.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@ApplicationScoped
public class UserServiceImpl {

    @GrpcClient
    UserServiceGrpc.UserServiceBlockingStub userService;

    public AddUserResponseDTO getUserById(String realmName, String userId) {

        AddUserResponseDTO jsonResponse = new AddUserResponseDTO();

        UserResponse kcResponse =
                userService.getUserById(GetUserByIdRequest.newBuilder()
                        .setRealmName(realmName)
                        .setUserId(userId).build());

        jsonResponse.setId(kcResponse.getUserDto().getId());
        jsonResponse.setCreatedTimestamp(kcResponse.getUserDto().getCreatedTimestamp());
        jsonResponse.setUserName(kcResponse.getUserDto().getUsername());
        jsonResponse.setEnabled(kcResponse.getUserDto().getEnabled());
        jsonResponse.setFirstName(kcResponse.getUserDto().getFirstName());
        jsonResponse.setLastName(kcResponse.getUserDto().getLastName());
        jsonResponse.setEmail(kcResponse.getUserDto().getEmail());
        jsonResponse.setRole(kcResponse.getUserDto().getRole());
        jsonResponse.setAttributes(kcResponse.getUserDto().getAttributesMap());

        return jsonResponse;
    }

    public UpdateUserPasswordResponseDTO updateUserPassword(UpdateUserPasswordDTO request) {

        UpdateUserPasswordResponseDTO jsonResponse = new UpdateUserPasswordResponseDTO();

        StatusReplay kcResponse =
                userService.updateUserPassword(UpdateUserPasswordRequest.newBuilder()
                        .setRealmName(request.getRealmName())
                        .setUserId(request.getUserId())
                        .setPassword(request.getPassword())
                        .build());

        jsonResponse.setStatusCode(kcResponse.getStatusCode());

        return jsonResponse;

    }

    public AddUserResponseDTO updateUser(UpdateUserDTO request) {

        AddUserResponseDTO jsonResponse = new AddUserResponseDTO();

        AddUserResponseDTO userAttributes = getUserById(request.getRealmName(), request.getUserId());

        UserResponse kcResponse =
                userService.updateUser(UpdateUserRequest.newBuilder()
                        .setRealmName(request.getRealmName())
                        .setUserId(request.getUserId())
                        .setUserName(request.getUserName().isEmpty() ? "" : request.getUserName().get())
                        .setFirstName(request.getFirstName().isEmpty() ? "" : request.getFirstName().get())
                        .setLastName(request.getLastName().isEmpty() ? "" : request.getLastName().get())
                        .setEmail(request.getEmail().isEmpty() ? "" : request.getEmail())
                        .setEnabled(request.getEnabled().isEmpty() ? userAttributes.isEnabled() : request.getEnabled().get())
                        .setRole(request.getRealmRole().isEmpty() ? "" : request.getRealmRole().get())
                        .setGroup(request.getGroup().isEmpty() ? "" : request.getGroup().get())
                        .putAllAttributes(request.getAttributes().isEmpty() ? new HashMap<>() : request.getAttributes().get())
                        .build());

        jsonResponse.setId(kcResponse.getUserDto().getId());
        jsonResponse.setCreatedTimestamp(kcResponse.getUserDto().getCreatedTimestamp());
        jsonResponse.setUserName(kcResponse.getUserDto().getUsername());
        jsonResponse.setEnabled(kcResponse.getUserDto().getEnabled());
        jsonResponse.setFirstName(kcResponse.getUserDto().getFirstName());
        jsonResponse.setLastName(kcResponse.getUserDto().getLastName());
        jsonResponse.setEmail(kcResponse.getUserDto().getEmail());
        jsonResponse.setAttributes(kcResponse.getUserDto().getAttributesMap());

        return jsonResponse;
    }

    public AddUserResponseDTO addUser(AddUserRequestDTO request) {

        AddUserResponseDTO jsonResponse = new AddUserResponseDTO();

        UserResponse kcResponse =
                userService.addUser(
                        AddUserRequest.newBuilder()
                                .setUserName(request.getUserName())
                                .setFirstName(request.getFirstName().isEmpty() ? "" : request.getFirstName().get())
                                .setLastName(request.getLastName().isEmpty() ? "" : request.getLastName().get())
                                .setEmail(request.getEmail())
                                .setRealmName(request.getRealmName())
                                .setRole(request.getRealmRole().isEmpty() ? "" : request.getRealmRole().get())
                                .setGroup(request.getGroup().isEmpty() ? "" : request.getGroup().get())
                                .putAllAttributes(request.getAttributes().isEmpty() ? new HashMap<>() : request.getAttributes().get())
                                .setCredentials(Credentials.newBuilder().setPassword(request.getCredentials().getPassword()).setTemporary(request.getCredentials().getTemporary()).build())
                                .build());

        jsonResponse.setId(kcResponse.getUserDto().getId());
        jsonResponse.setCreatedTimestamp(kcResponse.getUserDto().getCreatedTimestamp());
        jsonResponse.setUserName(kcResponse.getUserDto().getUsername());
        jsonResponse.setEnabled(kcResponse.getUserDto().getEnabled());
        jsonResponse.setFirstName(kcResponse.getUserDto().getFirstName());
        jsonResponse.setLastName(kcResponse.getUserDto().getLastName());
        jsonResponse.setEmail(kcResponse.getUserDto().getEmail());
        jsonResponse.setGroup(kcResponse.getUserDto().getGroup());
        jsonResponse.setAttributes(kcResponse.getUserDto().getAttributesMap());
        jsonResponse.setRole(kcResponse.getUserDto().getRole());


        return jsonResponse;
    }

    public UpdateUserAttributesResponse updateUserAttributes(managment.dto.user.UpdateUserAttributesRequest request) {

        UpdateUserAttributesResponse jsonResponse = new UpdateUserAttributesResponse();
        UserResponse kcResponse =
                userService.updateUserAttributes(com.sitech.users.UpdateUserAttributesRequest.newBuilder()
                        .setRealmName(request.getRealmName())
                        .setUserId(request.getUserId())
                        .putAllAttributes(request.getAttributes())
                        .build());

        jsonResponse.setId(kcResponse.getUserDto().getId());
        jsonResponse.setCreatedTimestamp(kcResponse.getUserDto().getCreatedTimestamp());
        jsonResponse.setUsername(kcResponse.getUserDto().getUsername());
        jsonResponse.setEnabled(kcResponse.getUserDto().getEnabled());
        jsonResponse.setFirstName(kcResponse.getUserDto().getFirstName().isEmpty() ? "" : kcResponse.getUserDto().getFirstName());
        jsonResponse.setLastName(kcResponse.getUserDto().getLastName().isEmpty() ? "" : kcResponse.getUserDto().getLastName());
        jsonResponse.setEmail(kcResponse.getUserDto().getEmail());
        jsonResponse.setAttributes(kcResponse.getUserDto().getAttributesMap().isEmpty() ? new HashMap<>() : kcResponse.getUserDto().getAttributesMap());

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

    public List<GetUserRoleAvailableResponseDTO> getUserRoleAvailable(
            String realmName, String userId) {

        List<GetUserRoleAvailableResponseDTO> jsonResponse = new ArrayList<>();

        GetUserRoleResponse KcResponse =
                userService.getUserRoleAvailable(
                        UserRoleRequest.newBuilder().setRealmName(realmName).setUserId(userId).build());

        for (int i = 0; i < KcResponse.getRoleDtoList().size(); i++) {
            GetUserRoleAvailableResponseDTO dto = new GetUserRoleAvailableResponseDTO();
            dto.setId(KcResponse.getRoleDto(i).getId());
            dto.setDescription(KcResponse.getRoleDto(i).getDescription());
            dto.setName(KcResponse.getRoleDto(i).getName());
            jsonResponse.add(dto);
        }
        return jsonResponse;
    }

    public List<FindAllUsersInGroupResponseDTO> findAllUsersInGroup(String realmName, String groupName) {

        List<FindAllUsersInGroupResponseDTO> jsonResponse = new ArrayList<>();

        UsersResponse KcResponse = userService.findAllUsersInGroup(
                UserGroupRequest.newBuilder().setRealmName(realmName).setGroupName(groupName).build());

        for (int i = 0; i < KcResponse.getUserDtoList().size(); i++) {
            FindAllUsersInGroupResponseDTO dto = new FindAllUsersInGroupResponseDTO();
            dto.setUsername(KcResponse.getUserDto(i).getUsername());
            dto.setFirstName(KcResponse.getUserDto(i).getFirstName().isEmpty() ? "" : KcResponse.getUserDto(i).getFirstName());
            dto.setLastName(KcResponse.getUserDto(i).getLastName().isEmpty() ? "" : KcResponse.getUserDto(i).getLastName());
            dto.setEmail(KcResponse.getUserDto(i).getEmail());
            dto.setEnabled(KcResponse.getUserDto(i).getEnabled());
            dto.setId(KcResponse.getUserDto(i).getId());
            dto.setCreatedTimestamp(KcResponse.getUserDto(i).getCreatedTimestamp());
            dto.setAttributes(KcResponse.getUserDto(i).getAttributesMap().isEmpty() ? new HashMap<>() : KcResponse.getUserDto(i).getAttributesMap());
            jsonResponse.add(dto);

        }
        return jsonResponse;
    }

    public KillUserSessionResponse killUserSession(KillUserSessionRequest req) {

        KillUserSessionResponse jsonResponse = new KillUserSessionResponse();

        UserStatusResponse KcResponse =
                userService.killUserSession(DeleteUserSessionRequest.newBuilder().setRealmName(req.getRealmName())
                        .setSessionState(req.getSessionState()).build());

        jsonResponse.setCode(KcResponse.getStatus());
        return jsonResponse;
    }

    public ResetUserPasswordResponse resetUserPassword(ResetUserPasswordReqDTO req) {

        ResetUserPasswordResponse jsonResponse = new ResetUserPasswordResponse();

        try{
            StatusReplay KcResponse =
                    userService.resetUserPassword(ResetUserPasswordRequest.newBuilder().setRealmName(req.getRealmName())
                            .setUserId(req.getUserId()).setUserName(req.getUserName())
                            .setOldPassword(req.getOldPassword()).setNewPassword(req.getNewPassword()).build());

            jsonResponse.setResponseMessage(KcResponse.getResponseMessage());
            jsonResponse.setStatusCode(KcResponse.getStatusCode());

            return jsonResponse;

        }catch (Exception e) {
            if (e.getMessage().contains("401")) {
                throw new UnauthorizedException("old password is wrong");
            }
        }
        return null;
    }

    public ResetUserPasswordResponse sendVerificationLink(SendVerificationLinkReq req){

        ResetUserPasswordResponse jsonResponse = new ResetUserPasswordResponse();

        StatusReplay KcResponse =
                userService.sendVerificationLink(SendVerificationLinkRequest.newBuilder().setRealmName(req.getRealmName())
                        .setUserId(req.getUserId()).build());

        jsonResponse.setResponseMessage(KcResponse.getResponseMessage());
        jsonResponse.setStatusCode(KcResponse.getStatusCode());

        return jsonResponse;
    }
}
