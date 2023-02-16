package managment.service;

import com.sitech.dto.Dto;
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
                        .setUserName(request.getUserName().isPresent() ? request.getUserName().get() : "")
                        .setFirstName(request.getFirstName().isPresent() ? request.getFirstName().get() : "")
                        .setLastName(request.getLastName().isPresent() ? request.getLastName().get() : "")
                        .setEmail(request.getEmail().isEmpty() ? "" : request.getEmail())
                        .setEnabled(request.getEnabled().isPresent() ? request.getEnabled().get() : userAttributes.isEnabled())
                        .setRole(request.getRealmRole().isPresent() ? request.getRealmRole().get() : "")
                        .setGroup(request.getGroup().isPresent() ? request.getGroup().get() : "")
                        .putAllAttributes(request.getAttributes().isPresent() ? request.getAttributes().get() : new HashMap<>())
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
                                .setFirstName(request.getFirstName().isPresent() ? request.getFirstName().get() : "")
                                .setLastName(request.getLastName().isPresent() ? request.getLastName().get() : "")
                                .setEmail(request.getEmail())
                                .setRealmName(request.getRealmName())
                                .setRole(request.getRealmRole().isPresent() ? request.getRealmRole().get() : "")
                                .setGroup(request.getGroup().isPresent() ? request.getGroup().get() : "")
                                .putAllAttributes(request.getAttributes().isPresent() ? request.getAttributes().get() : new HashMap<>())
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

        GetUserGroupsResponse kcResponse =
                userService.getUserGroups(
                        GetUserGroupRequest.newBuilder().setRealmName(realmName).setUserId(userId).build());

        for (int i = 0; i < kcResponse.getGroupDtoList().size(); i++) {
            GetUserGroupsResponseDTO dto = new GetUserGroupsResponseDTO();
            dto.setId(kcResponse.getGroupDto(i).getId());
            dto.setName(kcResponse.getGroupDto(i).getName());
            jsonResponse.add(dto);
        }

        return jsonResponse;
    }

    public List<GetUserRoleEffectiveResponseDTO> getUserRoleEffective(
            String realmName, String userId) {

        List<GetUserRoleEffectiveResponseDTO> jsonResponse = new ArrayList<>();

        GetUserRoleResponse kcResponse =
                userService.getUserRoleEffective(
                        UserRoleRequest.newBuilder().setRealmName(realmName).setUserId(userId).build());

        for (int i = 0; i < kcResponse.getRoleDtoList().size(); i++) {
            GetUserRoleEffectiveResponseDTO dto = new GetUserRoleEffectiveResponseDTO();
            dto.setId(kcResponse.getRoleDto(i).getId());
            dto.setDescription(kcResponse.getRoleDto(i).getDescription());
            dto.setName(kcResponse.getRoleDto(i).getName());
            jsonResponse.add(dto);
        }
        return jsonResponse;
    }

    public List<GetUserRoleAvailableResponseDTO> getUserRoleAvailable(
            String realmName, String userId) {

        List<GetUserRoleAvailableResponseDTO> jsonResponse = new ArrayList<>();

        GetUserRoleResponse kcResponse =
                userService.getUserRoleAvailable(
                        UserRoleRequest.newBuilder().setRealmName(realmName).setUserId(userId).build());

        for (int i = 0; i < kcResponse.getRoleDtoList().size(); i++) {
            GetUserRoleAvailableResponseDTO dto = new GetUserRoleAvailableResponseDTO();
            dto.setId(kcResponse.getRoleDto(i).getId());
            dto.setDescription(kcResponse.getRoleDto(i).getDescription());
            dto.setName(kcResponse.getRoleDto(i).getName());
            jsonResponse.add(dto);
        }
        return jsonResponse;
    }

    public List<FindAllUsersInGroupResponseDTO> findAllUsersInGroup(String realmName, String groupName) {

        List<FindAllUsersInGroupResponseDTO> jsonResponse = new ArrayList<>();

        UsersResponse kcResponse = userService.findAllUsersInGroup(
                UserGroupRequest.newBuilder().setRealmName(realmName).setGroupName(groupName).build());

        for (Dto.UserDto userDto : kcResponse.getUserDtoList()) {
            FindAllUsersInGroupResponseDTO dto = new FindAllUsersInGroupResponseDTO(userDto.getId(),
                    userDto.getCreatedTimestamp(),
                    userDto.getUsername(),
                    userDto.getEnabled(),
                    userDto.getFirstName().isEmpty() ? "" : userDto.getFirstName(),
                    userDto.getLastName().isEmpty() ? "" : userDto.getLastName(),
                    userDto.getEmail(),
                    userDto.getAttributesMap().isEmpty() ? new HashMap<>() : userDto.getAttributesMap());
            jsonResponse.add(dto);

        }
        return jsonResponse;
    }

    public KillUserSessionResponse killUserSession(KillUserSessionRequest req) {

        KillUserSessionResponse jsonResponse = new KillUserSessionResponse();

        UserStatusResponse kcResponse =
                userService.killUserSession(DeleteUserSessionRequest.newBuilder().setRealmName(req.getRealmName())
                        .setSessionState(req.getSessionState()).build());

        jsonResponse.setCode(kcResponse.getStatus());
        return jsonResponse;
    }

    public ResetUserPasswordResponse resetUserPassword(ResetUserPasswordReqDTO req) {

        ResetUserPasswordResponse jsonResponse = new ResetUserPasswordResponse();

        try{
            StatusReplay kcResponse =
                    userService.resetUserPassword(ResetUserPasswordRequest.newBuilder().setRealmName(req.getRealmName())
                            .setUserId(req.getUserId()).setUserName(req.getUserName())
                            .setOldPassword(req.getOldPassword()).setNewPassword(req.getNewPassword()).build());

            jsonResponse.setResponseMessage(kcResponse.getResponseMessage());
            jsonResponse.setStatusCode(kcResponse.getStatusCode());

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

        StatusReplay kcResponse =
                userService.sendVerificationLink(SendVerificationLinkRequest.newBuilder().setRealmName(req.getRealmName())
                        .setUserId(req.getUserId()).build());

        jsonResponse.setResponseMessage(kcResponse.getResponseMessage());
        jsonResponse.setStatusCode(kcResponse.getStatusCode());

        return jsonResponse;
    }
}
