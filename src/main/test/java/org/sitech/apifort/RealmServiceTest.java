package org.sitech.apifort;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.common.mapper.TypeRef;
import managment.Util.TestingProfile;
import managment.Util.Util;
import managment.constant.UnitTestConstants;
import managment.dto.realm.*;
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
class RealmServiceTest {

  String generatedRealmName = Util.getRealmName();

  @Test
  @Order(1)
  void getRealmByName() {

    given()
        .queryParam("realmName", UnitTestConstants.REALM_NAME)
        .when()
        .get("/realm/getRealmByName")
        .then()
        .statusCode(200)
        .body("id", equalTo("abrz"))
        .body("realm", equalTo("abrz"))
        .body("displayName", equalTo("abrz"))
        .body("enabled", equalTo(true));
  }

  @Test
  @Order(1)
  void getRealmGroups() {

    List<GetRealmGroupsResponseDTO> fetchedGroups =
        given()
            .queryParam("realmName", UnitTestConstants.REALM_NAME)
            .when()
            .get("http://localhost:9191/realm/getRealmGroups")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<>() {});

    assertNotNull(fetchedGroups);
    assertFalse(fetchedGroups.isEmpty());
    assertEquals("deac8d85-a6e5-4487-ab62-6af79310ec40", fetchedGroups.get(0).getId());
    assertEquals("abrzgroup", fetchedGroups.get(0).getName());
  }

  @Test
  @Order(1)
  void getRealmUsers() {

    List<GetRealmUsersResponseDTO> fetchedUsers =
        given()
            .queryParam("realmName", UnitTestConstants.REALM_NAME)
            .when()
            .get("http://localhost:9191/realm/getRealmUsers")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<>() {});

    assertNotNull(fetchedUsers);
    assertFalse(fetchedUsers.isEmpty());
    assertEquals("87a9d1a4-be29-4d5e-9f0a-523e7bf11316", fetchedUsers.get(0).getId());
    assertEquals(1660122905668L, fetchedUsers.get(0).getCreatedTimestamp());
    assertEquals("abrz", fetchedUsers.get(0).getUsername());
    assertEquals("MOHAMMAD", fetchedUsers.get(0).getFirstName());
    assertEquals("ABRZ", fetchedUsers.get(0).getLastName());
    assertEquals("sd", fetchedUsers.get(0).getEmail());
  }

  @Test
  @Order(1)
  void getRealms() {

    List<GetRealmsResponseDTO> fetchedRealms =
        given()
            .when()
            .get("http://localhost:9191/realm/getRealms")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<>() {});

    assertNotNull(fetchedRealms);
    assertFalse(fetchedRealms.isEmpty());
  }

  @Test
  @Order(2)
  void addRealm() {

    JsonObject jsonObject = Json.createObjectBuilder().add("realmName", generatedRealmName).build();

    AddRealmResponseDTO addedRealm =
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(jsonObject.toString())
            .when()
            .post("http://localhost:9191/realm/addRealm")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<>() {});

    assertNotNull(addedRealm);
    assertEquals(generatedRealmName, addedRealm.getId());
    assertEquals(generatedRealmName, addedRealm.getRealm());
    assertEquals(true, addedRealm.isEnabled());
  }

  @Test
  @Order(3)
  void addRealmGroup() {

    String generatedGroupName = "group" + generatedRealmName;

    JsonObject jsonObject =
        Json.createObjectBuilder()
            .add("realmName", generatedRealmName)
            .add("groupName", generatedGroupName)
            .build();


    AddRealmGroupResponseDTO addRealmGroup =
            given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonObject.toString())
                    .when()
                    .post("http://localhost:9191/realm/addRealmGroup")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<>() {});

    assertNotNull(addRealmGroup);
    assertEquals(201, addRealmGroup.getStatus());

  }
}
