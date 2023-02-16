package managment.service;


import com.sitech.access.PublicAccessServiceGrpc;
import com.sitech.access.PublicKeyReplay;
import com.sitech.access.PublicKeyRequest;
import io.quarkus.grpc.GrpcClient;
import managment.dto.access.GetCertificateResponseDTO;
import managment.dto.access.GetPublicKeyResponseDTO;

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

    public GetCertificateResponseDTO getCertificate(String realmName) {

        GetCertificateResponseDTO jsonResponse = new GetCertificateResponseDTO();

        PublicKeyReplay kcResponse = publicAccessService.getCertificate(PublicKeyRequest.newBuilder().
                setRealmName(realmName).build());

        jsonResponse.setValue(kcResponse.getValue());

        return jsonResponse;

    }
}
