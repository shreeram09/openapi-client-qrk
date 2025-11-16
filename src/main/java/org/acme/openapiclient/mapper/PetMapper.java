package org.acme.openapiclient.mapper;

import org.acme.openapiclient.dto.PetDTO;
import org.acme.openapiclient.petstore.model.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * MapStruct mapper for converting between Pet domain models and DTOs.
 *
 * MapStruct will automatically generate the implementation at compile time.
 * The generated class will be named PetMapperImpl and will be a CDI bean.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface PetMapper {

    /**
     * Converts a Pet model to a PetDTO.
     *
     * MapStruct will automatically map fields with the same name.
     * Null handling is done automatically.
     *
     * @param pet The Pet model
     * @return The corresponding PetDTO, or null if input is null
     */
    PetDTO toDTO(Pet pet);

    /**
     * Converts a PetDTO to a Pet model.
     *
     * MapStruct will automatically map fields with the same name.
     * Null handling is done automatically.
     *
     * @param dto The PetDTO
     * @return The corresponding Pet model, or null if input is null
     */
    Pet toModel(PetDTO dto);

    /**
     * Converts a list of Pet models to a list of PetDTOs.
     *
     * MapStruct automatically handles collection mappings.
     * Returns an empty list if input is null.
     *
     * @param pets The list of Pet models
     * @return The corresponding list of PetDTOs
     */
    List<PetDTO> toDTOList(List<Pet> pets);

    /**
     * Converts a list of PetDTOs to a list of Pet models.
     *
     * MapStruct automatically handles collection mappings.
     * Returns an empty list if input is null.
     *
     * @param dtos The list of PetDTOs
     * @return The corresponding list of Pet models
     */
    List<Pet> toModelList(List<PetDTO> dtos);
}

