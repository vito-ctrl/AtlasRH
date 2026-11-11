package com.example.AtlasRH.service.impl;

import com.example.AtlasRH.dto.contract.ContractRequestDto;
import com.example.AtlasRH.dto.contract.ContractResponseDto;
import com.example.AtlasRH.entity.Contract;
import com.example.AtlasRH.entity.ContractType;
import com.example.AtlasRH.entity.Employee;
import com.example.AtlasRH.exception.BusinessRuleException;
import com.example.AtlasRH.exception.ResourceNotFoundException;
import com.example.AtlasRH.mapper.ContractMapper;
import com.example.AtlasRH.repository.ContractRepository;
import com.example.AtlasRH.repository.EmployeeRepository;
import com.example.AtlasRH.service.ContractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContractServiceImpl implements ContractService {

    private static final Logger log = LoggerFactory.getLogger(ContractServiceImpl.class);

    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final ContractMapper contractMapper;

    public ContractServiceImpl(
            ContractRepository contractRepository,
            EmployeeRepository employeeRepository,
            ContractMapper contractMapper
    ) {
        this.contractRepository = contractRepository;
        this.employeeRepository = employeeRepository;
        this.contractMapper = contractMapper;
    }

    @Override
    public ContractResponseDto createContract(ContractRequestDto request) {
        log.info("Création d'un contrat pour l'employé id={}, type={}", request.getEmployeeId(), request.getContractType());

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employé", "id", request.getEmployeeId()));

        // Règle 3 : Un employé n'a qu'un seul contrat en cours
        if (contractRepository.existsByEmployeeIdAndActifTrue(employee.getId())) {
            throw new BusinessRuleException(
                    "L'employé possède déjà un contrat en cours. Veuillez terminer le contrat actuel avant d'en créer un nouveau.",
                    HttpStatus.CONFLICT
            );
        }

        // Règle 3 : Un CDD ou un STAGE doit obligatoirement avoir une date de fin
        if (request.getContractType() == ContractType.CDD || request.getContractType() == ContractType.STAGE) {
            if (request.getDateFin() == null) {
                throw new BusinessRuleException(
                        "Un contrat de type " + request.getContractType() + " doit obligatoirement comporter une date de fin."
                );
            }
            if (request.getDateFin().isBefore(request.getDateDebut())) {
                throw new BusinessRuleException(
                        "La date de fin ne peut pas être antérieure à la date de début du contrat."
                );
            }
        }

        Contract contract = contractMapper.toEntity(request);
        contract.setEmployee(employee);
        contract.setActif(true);

        Contract saved = contractRepository.save(contract);
        log.info("Contrat id={} créé avec succès pour l'employé id={}", saved.getId(), employee.getId());
        return contractMapper.toResponseDto(saved);
    }

    @Override
    public ContractResponseDto terminateContract(Long id) {
        log.info("Terminaison du contrat id={}", id);

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", "id", id));

        if (!Boolean.TRUE.equals(contract.getActif())) {
            throw new BusinessRuleException("Ce contrat est déjà terminé.");
        }

        contract.setActif(false);
        // Si la date de fin n'est pas définie ou est dans le futur, on la fixe à aujourd'hui
        if (contract.getDateFin() == null || contract.getDateFin().isAfter(LocalDate.now())) {
            contract.setDateFin(LocalDate.now());
        }

        Contract updated = contractRepository.save(contract);
        log.info("Contrat id={} terminé avec succès", id);
        return contractMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ContractResponseDto getContractById(Long id) {
        log.info("Consultation du contrat id={}", id);
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", "id", id));
        return contractMapper.toResponseDto(contract);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractResponseDto> getContractsByEmployeeId(Long employeeId) {
        log.info("Consultation de l'historique des contrats de l'employé id={}", employeeId);

        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employé", "id", employeeId);
        }

        return contractRepository.findByEmployeeIdOrderByDateDebutDesc(employeeId)
                .stream()
                .map(contractMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
