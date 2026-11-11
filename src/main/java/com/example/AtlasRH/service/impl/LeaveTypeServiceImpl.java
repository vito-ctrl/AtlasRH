package com.example.AtlasRH.service.impl;

import com.example.AtlasRH.dto.leave.LeaveTypeResponseDto;
import com.example.AtlasRH.entity.LeaveType;
import com.example.AtlasRH.exception.ResourceNotFoundException;
import com.example.AtlasRH.mapper.LeaveTypeMapper;
import com.example.AtlasRH.repository.LeaveTypeRepository;
import com.example.AtlasRH.service.LeaveTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeMapper leaveTypeMapper;

    public LeaveTypeServiceImpl(LeaveTypeRepository leaveTypeRepository, LeaveTypeMapper leaveTypeMapper) {
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveTypeMapper = leaveTypeMapper;
    }

    @Override
    public List<LeaveTypeResponseDto> getAllLeaveTypes() {
        return leaveTypeRepository.findAll()
                .stream()
                .map(leaveTypeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveTypeResponseDto getLeaveTypeById(Long id) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de congé", "id", id));
        return leaveTypeMapper.toResponseDto(leaveType);
    }
}
