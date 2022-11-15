package org.sitech.apifort;


import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.common.mapper.TypeRef;
import managment.Util.Util;
import managment.dto.KeycloackAuthDTO;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.given;
import static org.wildfly.common.Assert.assertTrue;


public class ContainorTest {

    public static final KeycloakContainer KEYCLOAK = new KeycloakContainer()
            .withRealmImportFile("/realm-export.json")
            .withReuse(true)
            .withAdminUsername("admin")
            .withAdminPassword("admin");


    @BeforeAll
    public static void beforeAll() {
        KEYCLOAK.start();
    }

    @AfterAll
    public static void afterAll() {
        KEYCLOAK.stop();
    }

    @Test
    @Order(1)
    void testKC() {

        assertTrue(KEYCLOAK.isRunning());
        String authServer = KEYCLOAK.getAuthServerUrl();
        System.out.println(authServer);
    }

    @Test
    @Order(1)
    void testAuthOnKeyCloak() {

        System.out.println(KEYCLOAK.getAuthServerUrl()+"realms/sitech/protocol/openid-connect/token");

        KeycloackAuthDTO dto =   given()
                        .contentType("application/x-www-form-urlencoded; charset=utf-8")
                        .formParam("client_id", "backend-client")
                        .formParam("client_secret", "dvD9YoMIiefPzmXV8oDa0jjAXoMoISz9")
                        .formParam("username","mohammad.ar@sitech.me")
                        .formParam("password", "123456")
                        .formParam("grant_type", "client_credentials")
                        .when()
                        .post(KEYCLOAK.getAuthServerUrl()+"realms/master/protocol/openid-connect/token")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(new TypeRef<>() {});


        String token = dto.getAccess_token();
        System.out.println(token);

    }


}
