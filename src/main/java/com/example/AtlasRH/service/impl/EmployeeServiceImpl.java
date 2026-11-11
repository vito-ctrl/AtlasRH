package com.example.AtlasRH.service.impl;

import com.example.AtlasRH.dto.common.PageResponseDto;
import com.example.AtlasRH.dto.employee.EmployeeHierarchyDto;
import com.example.AtlasRH.dto.employee.EmployeeRequestDto;
import com.example.AtlasRH.dto.employee.EmployeeResponseDto;
import com.example.AtlasRH.dto.leave.EmployeeLeaveBalanceDto;
import com.example.AtlasRH.dto.leave.LeaveBalanceItemDto;
import com.example.AtlasRH.entity.Department;
import com.example.AtlasRH.entity.Employee;
import com.example.AtlasRH.entity.LeaveType;
import com.example.AtlasRH.exception.BusinessRuleException;
import com.example.AtlasRH.exception.ResourceAlreadyExistsException;
import com.example.AtlasRH.exception.ResourceNotFoundException;
import com.example.AtlasRH.mapper.EmployeeMapper;
import com.example.AtlasRH.repository.DepartmentRepository;
import com.example.AtlasRH.repository.EmployeeRepository;
import com.example.AtlasRH.repository.LeaveRequestRepository;
import com.example.AtlasRH.repository.LeaveTypeRepository;
import com.example.AtlasRH.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            LeaveTypeRepository leaveTypeRepository,
            LeaveRequestRepository leaveRequestRepository,
            EmployeeMapper employeeMapper
    ) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public EmployeeResponseDto createEmployee(EmployeeRequestDto request) {
        log.info("Création d'un nouvel employé avec le matricule {}", request.getMatricule());

        // Règle 1 : Le matricule et l'e-mail sont uniques
        if (employeeRepository.existsByMatriculeIgnoreCase(request.getMatricule().trim())) {
            throw new ResourceAlreadyExistsException(
                    "Un employé avec le matricule '" + request.getMatricule().trim() + "' existe déjà."
            );
        }
        if (employeeRepository.existsByEmailIgnoreCase(request.getEmail().trim())) {
            throw new ResourceAlreadyExistsException(
                    "Un employé avec l'adresse e-mail '" + request.getEmail().trim() + "' existe déjà."
            );
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Département", "id", request.getDepartmentId()));

        Employee manager = null;
        if (request.getManagerId() != null) {
            manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager", "id", request.getManagerId()));
        }

        Employee employee = employeeMapper.toEntity(request);
        employee.setMatricule(request.getMatricule().trim());
        employee.setEmail(request.getEmail().trim().toLowerCase());
        employee.setDepartment(department);
        employee.setManager(manager);

        Employee saved = employeeRepository.save(employee);
        log.info("Employé créé avec succès avec l'identifiant {}", saved.getId());
        return employeeMapper.toResponseDto(saved);
    }

    @Override
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto request) {
        log.info("Mise à jour de l'employé id={}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", "id", id));

        // Règle 2 : Un employé ne peut pas être son propre manager
        if (request.getManagerId() != null && request.getManagerId().equals(id)) {
            throw new BusinessRuleException("Un employé ne peut pas être son propre manager.");
        }

        // Règle 1 : Vérification de l'unicité du matricule et de l'e-mail
        String newMatricule = request.getMatricule().trim();
        if (employeeRepository.existsByMatriculeIgnoreCaseAndIdNot(newMatricule, id)) {
            throw new ResourceAlreadyExistsException(
                    "Un autre employé possède déjà le matricule '" + newMatricule + "'."
            );
        }

        String newEmail = request.getEmail().trim().toLowerCase();
        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(newEmail, id)) {
            throw new ResourceAlreadyExistsException(
                    "Un autre employé possède déjà l'adresse e-mail '" + newEmail + "'."
            );
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Département", "id", request.getDepartmentId()));

        Employee manager = null;
        if (request.getManagerId() != null) {
            manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager", "id", request.getManagerId()));
        }

        employeeMapper.updateEntityFromDto(request, employee);
        employee.setMatricule(newMatricule);
        employee.setEmail(newEmail);
        employee.setDepartment(department);
        employee.setManager(manager);

        Employee updated = employeeRepository.save(employee);
        log.info("Employé id={} mis à jour avec succès", id);
        return employeeMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployeeById(Long id) {
        log.info("Consultation de l'employé id={}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", "id", id));
        return employeeMapper.toResponseDto(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<EmployeeResponseDto> getAllEmployees(
            String name,
            Long departmentId,
            String departmentName,
            Pageable pageable
    ) {
        log.info("Recherche et pagination des employés : nom={}, deptId={}, deptName={}",
                name, departmentId, departmentName);

        String trimmedName = (name != null && !name.isBlank()) ? name.trim() : null;
        String trimmedDeptName = (departmentName != null && !departmentName.isBlank()) ? departmentName.trim() : null;

        Page<Employee> page;
        if (trimmedName != null && departmentId != null) {
            page = employeeRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseAndDepartmentId(
                    trimmedName, trimmedName, departmentId, pageable
            );
        } else if (trimmedName != null) {
            page = employeeRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                    trimmedName, trimmedName, pageable
            );
        } else if (departmentId != null) {
            page = employeeRepository.findByDepartmentId(departmentId, pageable);
        } else if (trimmedDeptName != null) {
            page = employeeRepository.findByDepartmentNameContainingIgnoreCase(trimmedDeptName, pageable);
        } else {
            page = employeeRepository.findAll(pageable);
        }

        Page<EmployeeResponseDto> dtoPage = page.map(employeeMapper::toResponseDto);
        return PageResponseDto.fromPage(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getTeamByManagerId(Long managerId) {
        log.info("Consultation de l'équipe du manager id={}", managerId);

        if (!employeeRepository.existsById(managerId)) {
            throw new ResourceNotFoundException("Manager", "id", managerId);
        }

        return employeeRepository.findByManagerId(managerId)
                .stream()
                .map(employeeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeLeaveBalanceDto getEmployeeLeaveBalance(Long employeeId, Integer year) {
        log.info("Consultation du solde de congé et de l'ancienneté pour l'employé id={}, année={}", employeeId, year);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", "id", employeeId));

        int targetYear = (year != null) ? year : LocalDate.now().getYear();

        // Calcul de l'ancienneté (US6)
        Period period = Period.between(employee.getDateEmbauche(), LocalDate.now());
        String seniority = String.format("%d an(s) et %d mois", period.getYears(), period.getMonths());

        // Calcul du solde pour chaque type de congé (Règle 5 & US6)
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
        List<LeaveBalanceItemDto> balanceItems = new ArrayList<>();

        for (LeaveType type : leaveTypes) {
            int approvedDays = leaveRequestRepository.sumApprovedDaysForYear(employeeId, type.getId(), targetYear);
            int remainingDays = Math.max(0, type.getDefaultDaysPerYear() - approvedDays);

            balanceItems.add(new LeaveBalanceItemDto(
                    type.getId(),
                    type.getCode(),
                    type.getName(),
                    type.getDefaultDaysPerYear(),
                    approvedDays,
                    remainingDays
            ));
        }

        EmployeeLeaveBalanceDto balanceDto = new EmployeeLeaveBalanceDto();
        balanceDto.setEmployeeId(employee.getId());
        balanceDto.setMatricule(employee.getMatricule());
        balanceDto.setNom(employee.getNom());
        balanceDto.setPrenom(employee.getPrenom());
        balanceDto.setDateEmbauche(employee.getDateEmbauche());
        balanceDto.setSeniority(seniority);
        balanceDto.setYear(targetYear);
        balanceDto.setBalances(balanceItems);

        return balanceDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeHierarchyDto> getOrganizationTree() {
        log.info("Construction de l'organigramme hiérarchique de l'entreprise");

        // Récupération des managers de premier niveau (ceux sans manager)
        List<Employee> topManagers = employeeRepository.findByManagerIsNull();
        return topManagers.stream()
                .map(this::buildHierarchyNode)
                .collect(Collectors.toList());
    }

    private EmployeeHierarchyDto buildHierarchyNode(Employee employee) {
        EmployeeHierarchyDto node = employeeMapper.toHierarchyDto(employee);
        List<Employee> subordinates = employeeRepository.findByManagerId(employee.getId());
        List<EmployeeHierarchyDto> subNodes = subordinates.stream()
                .map(this::buildHierarchyNode)
                .collect(Collectors.toList());
        node.setSubordinates(subNodes);
        return node;
    }
}
