package managment.endpoints;

import managment.dto.access.GetPublicKeyResponseDTO;
import managment.dto.access.getCertificateResponseDTO;
import managment.service.AccessServiceImpl;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;


import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/access")
public class KCPublicAccessEndpoint {

    @Inject
    AccessServiceImpl service;

    @Path("/getPublicKey")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed(name = "timeGetAll", description = "How long it takes to invoke the getAll", unit = MetricUnits.MILLISECONDS)
    public GetPublicKeyResponseDTO getPublicKey(@QueryParam("realmName") String realmName) {
        return service.getPublicKey(realmName);
    }

    @Path("/getCertificate")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public getCertificateResponseDTO getCertificate(@QueryParam("realmName") String realmName) {
        return service.getCertificate(realmName);
    }
}
