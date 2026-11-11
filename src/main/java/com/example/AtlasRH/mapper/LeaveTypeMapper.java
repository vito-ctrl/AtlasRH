package com.example.AtlasRH.mapper;

import com.example.AtlasRH.dto.leave.LeaveTypeResponseDto;
import com.example.AtlasRH.entity.LeaveType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LeaveTypeMapper {

    LeaveTypeResponseDto toResponseDto(LeaveType leaveType);
}
