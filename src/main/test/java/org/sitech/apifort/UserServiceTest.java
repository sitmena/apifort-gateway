package org.sitech.apifort;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.common.mapper.TypeRef;
import managment.Util.TestingProfile;
import managment.constant.UnitTestConstants;
import managment.dto.user.AddUserResponseDTO;
import managment.dto.user.getUserRoleAvailableResponseDTO;
import org.junit.jupiter.api.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(TestingProfile.integration.class)
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

  @Test
  @Order(1)
  void addUser() {

    JsonObject jsonObject =
        Json.createObjectBuilder()
            .add("userName", "mohammad")
            .add("pass", "123456")
            .add("firstName", "mohammad")
            .add("lastName", "abrz")
            .add("email", "mohammad@yahoo.com")
            .add("realmName", UnitTestConstants.REALM_NAME)
            .add("realmRole", "realmRole")
            .build();

    AddUserResponseDTO addUser =
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(jsonObject.toString())
            .when()
            .post("http://localhost:9191/user/addUser")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<>() {});

    assertNotNull(addUser);
    assertEquals("mohammad", addUser.getUsername());
    assertEquals("mohammad@yahoo.com", addUser.getEmail());
  }

  @Test
  @Order(2)
  void getUserByUserName() {

    given()
        .queryParam("realmName", UnitTestConstants.REALM_NAME)
        .queryParam("userName", "mohammad")
        .when()
        .get("http://localhost:9191/user/getUserByUserName")
        .then()
        .statusCode(200)
        .body("id", equalTo("16cd2fe9-9a51-426c-b35f-8390ff3f23c6"));
  }

  @Test
  @Order(2)
  void getUserRoleAvailable() {

    List<getUserRoleAvailableResponseDTO> getUserRoleAvailableResponseDTOS =
        given()
            .queryParam("realmName", UnitTestConstants.REALM_NAME)
            .queryParam("userId", "77129475-2662-45cc-bcc3-5f102a2cd814")
            .when()
            .get("http://localhost:9191/user/getUserRoleAvailable")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<>() {});

    assertNotNull(getUserRoleAvailableResponseDTOS);
    assertFalse(getUserRoleAvailableResponseDTOS.isEmpty());
    assertEquals(
        "7066279d-6464-4e73-8bf8-c72ddb344d82", getUserRoleAvailableResponseDTOS.get(0).getId());
  }
}
