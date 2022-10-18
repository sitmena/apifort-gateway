package org.sitech.apifort;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import managment.Util.TestingProfile;
import managment.constant.UnitTestConstants;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
@Tag("unit")
@TestProfile(TestingProfile.unit.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccessServiceTest {

  @Test
  @Order(1)
  void getPublicKey() {

    given()
        .queryParam("realmName", UnitTestConstants.REALM_NAME)
        .when()
        .get("http://localhost:9191/access/getPublicKey")
        .then()
        .statusCode(200)
        .body("value", equalTo(UnitTestConstants.ABRZ_REALM_PUPLIC_KEY));
  }

  @Test
  @Order(1)
  void getCertificate() {

    given()
        .queryParam("realmName", UnitTestConstants.REALM_NAME)
        .when()
        .get("http://localhost:9191/access/getCertificate")
        .then()
        .statusCode(200)
        .body("value", equalTo(UnitTestConstants.ABRZ_REALM_PUPLIC_CERTIFICATE));
  }
}
