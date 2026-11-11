package com.example.AtlasRH.mapper;

import com.example.AtlasRH.dto.department.DepartmentRequestDto;
import com.example.AtlasRH.dto.department.DepartmentResponseDto;
import com.example.AtlasRH.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Department toEntity(DepartmentRequestDto dto);

    @Mapping(target = "employeeCount", expression = "java(department.getEmployees() != null ? department.getEmployees().size() : 0)")
    DepartmentResponseDto toResponseDto(Department department);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(DepartmentRequestDto dto, @MappingTarget Department department);
}
