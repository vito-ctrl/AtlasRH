package com.example.AtlasRH.mapper;

import com.example.AtlasRH.dto.leave.LeaveRequestResponseDto;
import com.example.AtlasRH.entity.LeaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveRequestMapper {

    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeMatricule", source = "employee.matricule")
    @Mapping(target = "employeeFullName", expression = "java(leaveRequest.getEmployee() != null ? leaveRequest.getEmployee().getPrenom() + \" \" + leaveRequest.getEmployee().getNom() : null)")
    @Mapping(target = "leaveTypeId", source = "leaveType.id")
    @Mapping(target = "leaveTypeCode", source = "leaveType.code")
    @Mapping(target = "leaveTypeName", source = "leaveType.name")
    LeaveRequestResponseDto toResponseDto(LeaveRequest leaveRequest);
}
