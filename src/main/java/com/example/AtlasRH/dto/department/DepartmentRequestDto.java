package com.example.AtlasRH.dto.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DepartmentRequestDto {

    @NotBlank(message = "Le nom du département est obligatoire")
    @Size(max = 100, message = "Le nom du département ne peut pas dépasser 100 caractères")
    private String name;

    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    private String description;

    public DepartmentRequestDto() {
    }

    public DepartmentRequestDto(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
