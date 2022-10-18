package managment.endpoints;

import managment.dto.user.*;
import managment.service.UserServiceImpl;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

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

    @Path("/getUserByUserName")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GetUserByUserNameResponseDTO getUserByUserName(
            @QueryParam("realmName") String realmName, @QueryParam("userName") String userName) {

        return service.getUserByUserName(realmName, userName);
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
    public List<getUserRoleAvailableResponseDTO> getUserRoleAvailable(
            @QueryParam("realmName") String realmName, @QueryParam("userId") String userId) {
        return service.getUserRoleAvailable(realmName, userId);
    }

    @Path("/findAllUsersInGroup")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<findAllUsersInGroupResponseDTO> findAllUsersInGroup(@QueryParam("realmName") String realmName,
                                                                    @QueryParam("groupName") String groupName) {
        return service.findAllUsersInGroup(realmName, groupName);
    }

    @Path("/updateUser")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AddUserResponseDTO updateUser(updateUserDTO request) {
        return service.updateUser(request);
    }

    @Path("/updateUser")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public updateUserPasswordResponseDTO updateUserPassword(updateUserPasswordDTO request) {
        return service.updateUserPassword(request);
    }
}
