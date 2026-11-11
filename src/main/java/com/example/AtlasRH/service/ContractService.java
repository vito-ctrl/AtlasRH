package com.example.AtlasRH.service;

import com.example.AtlasRH.dto.contract.ContractRequestDto;
import com.example.AtlasRH.dto.contract.ContractResponseDto;

import java.util.List;

public interface ContractService {

    ContractResponseDto createContract(ContractRequestDto request);

    ContractResponseDto terminateContract(Long id);

    ContractResponseDto getContractById(Long id);

    List<ContractResponseDto> getContractsByEmployeeId(Long employeeId);
}
