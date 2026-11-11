package com.example.AtlasRH.service;

import com.example.AtlasRH.dto.leave.LeaveDecisionDto;
import com.example.AtlasRH.dto.leave.LeaveRequestCreateDto;
import com.example.AtlasRH.dto.leave.LeaveRequestResponseDto;
import com.example.AtlasRH.entity.LeaveStatus;

import java.util.List;

public interface LeaveRequestService {

    // US5 : Employé
    LeaveRequestResponseDto createLeaveRequest(LeaveRequestCreateDto request);

    List<LeaveRequestResponseDto> getMyLeaveRequests(Long employeeId, LeaveStatus status);

    LeaveRequestResponseDto cancelLeaveRequest(Long requestId, Long employeeId);

    // US4 : Manager
    List<LeaveRequestResponseDto> getPendingRequestsForManagerTeam(Long managerId);

    LeaveRequestResponseDto approveRequest(Long requestId, LeaveDecisionDto decision);

    LeaveRequestResponseDto rejectRequest(Long requestId, LeaveDecisionDto decision);

    // Consultation générique
    LeaveRequestResponseDto getLeaveRequestById(Long requestId);
}
