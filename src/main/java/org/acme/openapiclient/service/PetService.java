package org.acme.openapiclient.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.acme.openapiclient.petstore.api.DefaultApi;
import org.acme.openapiclient.petstore.model.Pet;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for Pet Store operations.
 * Handles business logic and delegates to the generated OpenAPI client.
 * Falls back to mock data when external API is unavailable.
 */
@ApplicationScoped
public class PetService {

    private static final Logger log = Logger.getLogger(PetService.class);

    @Inject
    @RestClient
    DefaultApi petStoreApi;

    /**
     * Creates mock pet data for demonstration purposes.
     * Used when external Pet Store API is unavailable.
     */
    private List<Pet> getMockPets() {
        List<Pet> mockPets = new ArrayList<>();
        
        Pet pet1 = new Pet();
        pet1.setId(1L);
        pet1.setName("Fluffy");
        pet1.setTag("cat");
        mockPets.add(pet1);
        
        Pet pet2 = new Pet();
        pet2.setId(2L);
        pet2.setName("Buddy");
        pet2.setTag("dog");
        mockPets.add(pet2);
        
        Pet pet3 = new Pet();
        pet3.setId(3L);
        pet3.setName("Tweety");
        pet3.setTag("bird");
        mockPets.add(pet3);
        
        log.info("Returning mock pet data (external API unavailable)");
        return mockPets;
    }

    /**
     * Retrieves all pets with an optional limit.
     * Falls back to mock data if external API is unavailable.
     *
     * @param limit Maximum number of pets to return
     * @return List of pets
     */
    public List<Pet> getAllPets(Integer limit) {
        log.debugf("Fetching pets with limit: %s", limit);

        List<Pet> pets = tryGetPetsFromExternalApi(limit);
        if (pets != null) {
            return pets;
        }

        // Fallback to mock data
        return applyLimitToMockPets(limit);
    }

    /**
     * Attempts to retrieve pets from external API.
     * Returns null if API is unavailable or fails.
     */
    private List<Pet> tryGetPetsFromExternalApi(Integer limit) {
        try {
            List<Pet> pets;
            try (Response response = petStoreApi.listPets(limit)) {
                if (response.getStatus() != 200) {
                    log.warnf("External API returned status: %d. Using mock data.", response.getStatus());
                    return null;
                }

                pets = response.readEntity(new GenericType<List<Pet>>() {
                });
            }
            log.infof("Successfully retrieved %d pets from external API", pets != null ? pets.size() : 0);
            return pets;

        } catch (Exception e) {
            log.warnf(e, "External Pet Store API unavailable: %s. Using mock data.", e.getMessage());
            return null;
        }
    }

    /**
     * Returns mock pets with optional limit applied.
     */
    private List<Pet> applyLimitToMockPets(Integer limit) {
        List<Pet> mockPets = getMockPets();
        if (limit != null && limit > 0 && limit < mockPets.size()) {
            return mockPets.subList(0, limit);
        }
        return mockPets;
    }

    /**
     * Retrieves a specific pet by ID.
     * Falls back to mock data if external API is unavailable.
     *
     * @param petId The pet identifier
     * @return The pet if found
     */
    public Pet getPetById(String petId) {
        log.debugf("Fetching pet with ID: %s", petId);

        Pet pet = tryGetPetFromExternalApi(petId);
        if (pet != null) {
            return pet;
        }

        // Fallback to mock data
        return findMockPetById(petId);
    }

    /**
     * Attempts to retrieve a pet from external API.
     * Returns null if API is unavailable or pet not found.
     */
    private Pet tryGetPetFromExternalApi(String petId) {
        try {
            Pet pet;
            try (Response response = petStoreApi.showPetById(petId)) {
                if (response.getStatus() != 200) {
                    log.warnf("Pet not found with ID: %s. Status: %d. Checking mock data.", petId, response.getStatus());
                    return null;
                }

                pet = response.readEntity(Pet.class);
            }
            log.infof("Successfully retrieved pet from external API: %s", petId);
            return pet;

        } catch (Exception e) {
            log.warnf(e, "External Pet Store API unavailable: %s. Using mock data.", e.getMessage());
            return null;
        }
    }

    /**
     * Finds a pet from mock data by ID.
     */
    private Pet findMockPetById(String petId) {
        try {
            long id = Long.parseLong(petId);
            return getMockPets().stream()
                    .filter(pet -> pet.getId() != null && pet.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        } catch (NumberFormatException e) {
            log.warnf("Invalid pet ID format: %s", petId);
            return null;
        }
    }

    /**
     * Creates a new pet.
     * Simulates creation if external API is unavailable.
     *
     * @param pet The pet to create
     * @return The created pet
     */
    public Pet createPet(Pet pet) {
        log.debugf("Creating new pet: %s", pet != null ? pet.getName() : "null");

        Pet createdPet = tryCreatePetInExternalApi(pet);
        if (createdPet != null) {
            return createdPet;
        }

        // Fallback to simulated creation
        return simulatePetCreation(pet);
    }

    /**
     * Attempts to create a pet in external API.
     * Returns null if API is unavailable or creation fails.
     */
    private Pet tryCreatePetInExternalApi(Pet pet) {
        try {
            Pet createdPet;
            try (Response response = petStoreApi.createPets(pet)) {
                if (response.getStatus() != 201) {
                    log.warnf("External API returned status: %d. Simulating pet creation.", response.getStatus());
                    return null;
                }

                createdPet = response.readEntity(Pet.class);
            }
            log.infof("Successfully created pet in external API: %s", createdPet.getName());
            return createdPet;

        } catch (Exception e) {
            log.warnf(e, "External Pet Store API unavailable: %s. Simulating pet creation.", e.getMessage());
            return null;
        }
    }

    /**
     * Simulates pet creation when external API is unavailable.
     */
    private Pet simulatePetCreation(Pet pet) {
        if (pet.getId() == null) {
            pet.setId(System.currentTimeMillis()); // Generate ID
        }
        log.infof("Simulated pet creation (mock mode): %s", pet.getName());
        return pet;
    }
}
