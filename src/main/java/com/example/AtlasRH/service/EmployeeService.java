package com.example.AtlasRH.service;

import com.example.AtlasRH.dto.common.PageResponseDto;
import com.example.AtlasRH.dto.employee.EmployeeHierarchyDto;
import com.example.AtlasRH.dto.employee.EmployeeRequestDto;
import com.example.AtlasRH.dto.employee.EmployeeResponseDto;
import com.example.AtlasRH.dto.leave.EmployeeLeaveBalanceDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDto createEmployee(EmployeeRequestDto request);

    EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto request);

    EmployeeResponseDto getEmployeeById(Long id);

    PageResponseDto<EmployeeResponseDto> getAllEmployees(
            String name,
            Long departmentId,
            String departmentName,
            Pageable pageable
    );

    List<EmployeeResponseDto> getTeamByManagerId(Long managerId);

    EmployeeLeaveBalanceDto getEmployeeLeaveBalance(Long employeeId, Integer year);

    List<EmployeeHierarchyDto> getOrganizationTree();
}
