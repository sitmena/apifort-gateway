package managment.endpoints;


import io.swagger.v3.oas.annotations.parameters.RequestBody;
import managment.dto.realm.*;
import managment.service.RealmServiceImpl;
import org.apache.camel.Body;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/realm")
public class KCRealmEndpoint {

    @Inject
    RealmServiceImpl service;

    @Path("/addRealm")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)

    public AddRealmResponseDTO addRealm(AddRealmRequestDTO request) {
        return service.addRealm(request.getRealmName());
    }

    @GET
    @Path("/getRealms")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<GetRealmsResponseDTO> getRealms() {
        return service.getRealms();
    }


    @Path("addRealmGroup")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)

    public AddRealmGroupResponseDTO addRealmGroup(AddRealmGroupRequestDTO request) {
        return service.AddRealmGroup(request);
    }


    @GET
    @Path("/getRealmByName")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GetRealmByNameResponseDTO getRealmByName(@QueryParam("realmName") String realmName) {
        return service.getRealmByName(realmName);
    }

    @GET
    @Path("/getRealmUsers")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<GetRealmUsersResponseDTO> getRealmUsers(@QueryParam("realmName") String realmName) {
        return service.getRealmUsers(realmName);
    }

    @GET
    @Path("/getRealmGroups")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<GetRealmGroupsResponseDTO> getRealmGroups(@QueryParam("realmName") String realmName) {

        return service.getRealmGroups(realmName);
    }

    @POST
    @Path("/logoutAllUsers")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public LogoutAllUsersResponse logoutAllUsers(LogoutAllUsersRequest req){
        return service.LogoutAllUsers(req.getRealmName());
    }



}


