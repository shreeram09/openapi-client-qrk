package org.acme.openapiclient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for Pet requests and responses.
 */
public class PetDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("tag")
    private String tag;

    public PetDTO() {
    }

    public PetDTO(Long id, String name, String tag) {
        this.id = id;
        this.name = name;
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "PetDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}

