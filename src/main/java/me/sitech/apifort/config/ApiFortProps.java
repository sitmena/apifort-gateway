package me.sitech.apifort.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

import java.util.List;


@ConfigMapping(prefix = "apifort")
public interface ApiFortProps {

    @WithName("admin")
    Admin admin();

    @WithName("cache")
    Cache cache();

    interface Admin {

        @WithName("public-certificate")
        String certificate();

        @WithName("realm")
        String realm();

        @WithName("api-key")
        String apikey();

        @WithName("allowed-headers")
        String allowedHeaders();

        @WithName("allowed-origin")
        String allowedOrigin();

        @WithName("enable-cors")
        boolean enableCors();

        @WithName("public-context")
        String publicContext();

        @WithName("private-context")
        String privateContext();

        @WithName("seconds-clock-skew")
        int clockSkewSeconds();

        @WithName("token-issuer")
        String tokenIssuer();

        @WithName("methods")
        Methods methods();

        interface Methods {
            @WithName("public-access")
            List<String> publicAccess();

            @WithName("private-access")
            List<String> privateAccess();
        }
    }

    interface Cache {
        @WithName("version")
        int version();
    }
}
