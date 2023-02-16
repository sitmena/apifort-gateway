package managment.endpoints;

import lombok.extern.slf4j.Slf4j;
import managment.dto.user.*;
import managment.service.UserServiceImpl;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Slf4j
@Path("/user")
public class KCUserEndpoint {

    @Inject
    UserServiceImpl service;

    @Path("/addUser")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AddUserResponseDTO addUser(AddUserRequestDTO request) {
        return service.addUser(request);
    }
    @Path("/updateUserAttributes")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UpdateUserAttributesResponse updateUserAttributes(UpdateUserAttributesRequest request) {
        return service.updateUserAttributes(request);
    }

    @Path("/getUserByUserName")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GetUserByUserNameResponseDTO getUserByUserName(
            @QueryParam("realmName") String realmName, @QueryParam("userName") String userName) {

        return service.getUserByUserName(realmName, userName);
    }

    @Path("/getUserById")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AddUserResponseDTO getUserById(
            @QueryParam("realmName") String realmName, @QueryParam("userId") String userId) {

        return service.getUserById(realmName, userId);
    }

    @Path("/getUserGroups")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<GetUserGroupsResponseDTO> getUserGroups(
            @QueryParam("realmName") String realmName, @QueryParam("userId") String userId) {
        return service.getUserGroups(realmName, userId);
    }

    @Path("/getUserRoleEffective")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<GetUserRoleEffectiveResponseDTO> getUserRoleEffective(
            @QueryParam("realmName") String realmName, @QueryParam("userId") String userId) {
        return service.getUserRoleEffective(realmName, userId);
    }

    @Path("/getUserRoleAvailable")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<GetUserRoleAvailableResponseDTO> getUserRoleAvailable(
            @QueryParam("realmName") String realmName, @QueryParam("userId") String userId) {
        return service.getUserRoleAvailable(realmName, userId);
    }

    @Path("/findAllUsersInGroup")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<FindAllUsersInGroupResponseDTO> findAllUsersInGroup(@QueryParam("realmName") String realmName,
                                                                    @QueryParam("groupName") String groupName) {
        return service.findAllUsersInGroup(realmName, groupName);
    }

    @Path("/updateUser")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AddUserResponseDTO updateUser(UpdateUserDTO request) {
        return service.updateUser(request);
    }

    @Path("/updateUserPassword")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UpdateUserPasswordResponseDTO updateUserPassword(UpdateUserPasswordDTO request) {
        return service.updateUserPassword(request);
    }

    @Path("/killUserSession")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public KillUserSessionResponse killUserSession(KillUserSessionRequest req){
        return service.killUserSession(req);
    }

    @Path("/resetUserPassword")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ResetUserPasswordResponse resetUserPassword(ResetUserPasswordReqDTO req){
        return service.resetUserPassword(req);
    }


    @Path("/sendVerificationLink")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ResetUserPasswordResponse sendVerificationLink(SendVerificationLinkReq req){
        return service.sendVerificationLink(req);
    }
}
