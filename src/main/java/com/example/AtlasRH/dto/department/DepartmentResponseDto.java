package com.example.AtlasRH.dto.department;

import java.time.LocalDateTime;

public class DepartmentResponseDto {

    private Long id;
    private String name;
    private String description;
    private int employeeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DepartmentResponseDto() {
    }

    public DepartmentResponseDto(Long id, String name, String description, int employeeCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.employeeCount = employeeCount;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
