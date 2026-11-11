package com.example.AtlasRH.service;

import com.example.AtlasRH.dto.leave.LeaveTypeResponseDto;

import java.util.List;

public interface LeaveTypeService {

    List<LeaveTypeResponseDto> getAllLeaveTypes();

    LeaveTypeResponseDto getLeaveTypeById(Long id);
}
