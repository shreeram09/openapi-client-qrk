package org.acme.openapiclient;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.openapiclient.petstore.model.Pet;
import org.acme.openapiclient.service.PetService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the Pet Store Client REST API with mocked service layer.
 * These tests use Mockito to mock the PetService, avoiding external API calls.
 */
@QuarkusTest
public class MyResourceMockedTest {

    @InjectMock
    PetService petService;

    /**
     * Test GET /api/pets with mocked service returning a list of pets.
     */
    @Test
    public void testGetAllPetsWithMockedService() {
        // Setup mock data
        Pet pet1 = new Pet();
        pet1.setId(1L);
        pet1.setName("Fluffy");
        pet1.setTag("cat");

        Pet pet2 = new Pet();
        pet2.setId(2L);
        pet2.setName("Buddy");
        pet2.setTag("dog");

        List<Pet> mockPets = Arrays.asList(pet1, pet2);

        // Mock the service
        when(petService.getAllPets(anyInt())).thenReturn(mockPets);

        // Test the endpoint
        given()
                .queryParam("limit", 10)
                .when().get("/api/pets")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(2))
                .body("[0].name", is("Fluffy"))
                .body("[0].tag", is("cat"))
                .body("[1].name", is("Buddy"))
                .body("[1].tag", is("dog"));
    }

    /**
     * Test GET /api/pets with no query parameter.
     */
    @Test
    public void testGetAllPetsWithoutLimit() {
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("Max");
        pet.setTag("dog");

        when(petService.getAllPets(null)).thenReturn(List.of(pet));

        given()
                .when().get("/api/pets")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(1))
                .body("[0].name", is("Max"));
    }

    /**
     * Test GET /api/pets/{petId} with mocked service.
     */
    @Test
    public void testGetPetByIdWithMockedService() {
        Pet mockPet = new Pet();
        mockPet.setId(123L);
        mockPet.setName("Whiskers");
        mockPet.setTag("cat");

        when(petService.getPetById("123")).thenReturn(mockPet);

        given()
                .pathParam("petId", "123")
                .when().get("/api/pets/{petId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", is(123))
                .body("name", is("Whiskers"))
                .body("tag", is("cat"));
    }

    /**
     * Test GET /api/pets/{petId} when pet not found.
     */
    @Test
    public void testGetPetByIdNotFound() {
        when(petService.getPetById("999")).thenReturn(null);

        given()
                .pathParam("petId", "999")
                .when().get("/api/pets/{petId}")
                .then()
                .statusCode(404);
    }

    /**
     * Test POST /api/pets with valid data and mocked service.
     */
    @Test
    public void testCreatePetWithMockedService() {
        Pet mockCreatedPet = new Pet();
        mockCreatedPet.setId(100L);
        mockCreatedPet.setName("Luna");
        mockCreatedPet.setTag("cat");

        when(petService.createPet(any(Pet.class))).thenReturn(mockCreatedPet);

        String petJson = """
                {
                    "name": "Luna",
                    "tag": "cat"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(petJson)
                .when().post("/api/pets")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", is(100))
                .body("name", is("Luna"))
                .body("tag", is("cat"));
    }

    /**
     * Test GET /api/pets returns empty list.
     */
    @Test
    public void testGetAllPetsReturnsEmptyList() {
        when(petService.getAllPets(any())).thenReturn(List.of());

        given()
                .when().get("/api/pets")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(0));
    }
}

