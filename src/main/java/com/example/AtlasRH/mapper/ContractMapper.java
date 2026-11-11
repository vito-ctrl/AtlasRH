package com.example.AtlasRH.mapper;

import com.example.AtlasRH.dto.contract.ContractRequestDto;
import com.example.AtlasRH.dto.contract.ContractResponseDto;
import com.example.AtlasRH.entity.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "actif", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Contract toEntity(ContractRequestDto dto);

    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeMatricule", source = "employee.matricule")
    @Mapping(target = "employeeFullName", expression = "java(contract.getEmployee() != null ? contract.getEmployee().getPrenom() + \" \" + contract.getEmployee().getNom() : null)")
    ContractResponseDto toResponseDto(Contract contract);
}
