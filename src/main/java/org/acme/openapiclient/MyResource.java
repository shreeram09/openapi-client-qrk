package org.acme.openapiclient;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.openapiclient.dto.PetDTO;
import org.acme.openapiclient.mapper.PetMapper;
import org.acme.openapiclient.petstore.model.Pet;
import org.acme.openapiclient.service.PetService;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * REST Resource for Pet Store Client operations.
 * This controller demonstrates the usage of the generated OpenAPI client.
 */
@Path("/api/pets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MyResource {

    private static final Logger log = Logger.getLogger(MyResource.class);

    @Inject
    PetService petService;

    @Inject
    PetMapper petMapper;

    /**
     * Welcome endpoint to verify the service is running.
     *
     * @return Welcome message
     */
    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public String welcome() {
        log.info("Health check endpoint called");
        return "Pet Store Client is up and running!";
    }

    /**
     * Get all pets with optional limit.
     *
     * @param limit Maximum number of pets to return (optional)
     * @return List of pets
     */
    @GET
    public Response getAllPets(@QueryParam("limit") Integer limit) {
        log.infof("GET /api/pets called with limit: %s", limit);
        try {
            List<Pet> pets = petService.getAllPets(limit);
            List<PetDTO> petDTOs = petMapper.toDTOList(pets);
            return Response.ok(petDTOs).build();
        } catch (Exception e) {
            log.errorf(e, "Error fetching pets");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to fetch pets")
                    .build();
        }
    }

    /**
     * Get a specific pet by ID.
     *
     * @param petId The pet identifier
     * @return The pet if found
     */
    @GET
    @Path("/{petId}")
    public Response getPetById(@PathParam("petId") String petId) {
        log.infof("GET /api/pets/%s called", petId);
        try {
            Pet pet = petService.getPetById(petId);
            if (pet == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Pet not found with ID: " + petId)
                        .build();
            }
            PetDTO petDTO = petMapper.toDTO(pet);
            return Response.ok(petDTO).build();
        } catch (Exception e) {
            log.errorf(e, "Error fetching pet with ID: %s", petId);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to fetch pet")
                    .build();
        }
    }

    /**
     * Create a new pet.
     *
     * @param petDTO The pet data to create
     * @return The created pet
     */
    @POST
    public Response createPet(PetDTO petDTO) {
        log.infof("POST /api/pets called with pet: %s", petDTO);
        try {
            if (petDTO.getName() == null || petDTO.getName().isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Pet name is required")
                        .build();
            }
            Pet pet = petMapper.toModel(petDTO);
            Pet createdPet = petService.createPet(pet);
            PetDTO createdPetDTO = petMapper.toDTO(createdPet);
            return Response.status(Response.Status.CREATED)
                    .entity(createdPetDTO)
                    .build();
        } catch (Exception e) {
            log.errorf(e, "Error creating pet");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to create pet")
                    .build();
        }
    }
}
