package managment.service;


import com.sitech.access.PublicAccessServiceGrpc;
import com.sitech.access.PublicKeyReplay;
import com.sitech.access.PublicKeyRequest;
import io.quarkus.grpc.GrpcClient;
import managment.dto.access.GetPublicKeyResponseDTO;
import managment.dto.access.getCertificateResponseDTO;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccessServiceImpl {

    @GrpcClient
    PublicAccessServiceGrpc.PublicAccessServiceBlockingStub publicAccessService;

    public GetPublicKeyResponseDTO getPublicKey(String realmName) {

        GetPublicKeyResponseDTO jsonResponse = new GetPublicKeyResponseDTO();

        PublicKeyReplay kcResponse = publicAccessService.getPublicKey(PublicKeyRequest.newBuilder().
                setRealmName(realmName).build());

        jsonResponse.setValue(kcResponse.getValue());

        return jsonResponse;

    }

    public getCertificateResponseDTO getCertificate(String realmName) {

        getCertificateResponseDTO jsonResponse = new getCertificateResponseDTO();

        PublicKeyReplay kcResponse = publicAccessService.getCertificate(PublicKeyRequest.newBuilder().
                setRealmName(realmName).build());

        jsonResponse.setValue(kcResponse.getValue());

        return jsonResponse;

    }
}
