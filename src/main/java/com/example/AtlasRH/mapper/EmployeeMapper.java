package com.example.AtlasRH.mapper;

import com.example.AtlasRH.dto.employee.EmployeeHierarchyDto;
import com.example.AtlasRH.dto.employee.EmployeeRequestDto;
import com.example.AtlasRH.dto.employee.EmployeeResponseDto;
import com.example.AtlasRH.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "subordinates", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "leaveRequests", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Employee toEntity(EmployeeRequestDto dto);

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "managerFullName", source = "manager", qualifiedByName = "formatManagerName")
    EmployeeResponseDto toResponseDto(Employee employee);

    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "subordinates", ignore = true)
    EmployeeHierarchyDto toHierarchyDto(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "subordinates", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "leaveRequests", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(EmployeeRequestDto dto, @MappingTarget Employee employee);

    @Named("formatManagerName")
    default String formatManagerName(Employee manager) {
        if (manager == null) {
            return null;
        }
        return manager.getPrenom() + " " + manager.getNom();
    }
}
