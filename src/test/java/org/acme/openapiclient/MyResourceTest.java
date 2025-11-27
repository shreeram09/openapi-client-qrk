package org.acme.openapiclient;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

/**
 * Integration tests for the Pet Store Client REST API.
 * <p>
 * Note: These tests verify the application endpoints work correctly.
 * Tests that interact with the external Pet Store API will fail if the API is not running.
 * In a production environment, you would mock the external API or use WireMock.
 */
@QuarkusTest
public class MyResourceTest {

    /**
     * Test the health check endpoint.
     * This test should always pass as it doesn't depend on external services.
     */
    @Test
    public void testHealthEndpoint() {
        given()
                .when().get("/api/pets/health")
                .then()
                .statusCode(200)
                .contentType(ContentType.TEXT)
                .body(is("Pet Store Client is up and running!"));
    }

    /**
     * Test validation for creating a pet without a name.
     * This should fail with 400 Bad Request.
     */
    @Test
    public void testCreatePetWithoutName() {
        String petJson = """
                {
                    "tag": "dog"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(petJson)
                .when().post("/api/pets")
                .then()
                .statusCode(400);
    }

    /**
     * Test validation for creating a pet with an empty name.
     * This should fail with 400 Bad Request.
     */
    @Test
    public void testCreatePetWithEmptyName() {
        String petJson = """
                {
                    "name": "",
                    "tag": "dog"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(petJson)
                .when().post("/api/pets")
                .then()
                .statusCode(400);
    }

    /**
     * Test validation for creating a pet with blank name.
     * This should fail with 400 Bad Request.
     */
    @Test
    public void testCreatePetWithBlankName() {
        String petJson = """
                {
                    "name": "   ",
                    "tag": "dog"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(petJson)
                .when().post("/api/pets")
                .then()
                .statusCode(400);
    }
}

