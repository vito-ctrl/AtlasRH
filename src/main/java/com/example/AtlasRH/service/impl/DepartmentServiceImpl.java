package com.example.AtlasRH.service.impl;

import com.example.AtlasRH.dto.department.DepartmentRequestDto;
import com.example.AtlasRH.dto.department.DepartmentResponseDto;
import com.example.AtlasRH.entity.Department;
import com.example.AtlasRH.exception.BusinessRuleException;
import com.example.AtlasRH.exception.ResourceAlreadyExistsException;
import com.example.AtlasRH.exception.ResourceNotFoundException;
import com.example.AtlasRH.mapper.DepartmentMapper;
import com.example.AtlasRH.repository.DepartmentRepository;
import com.example.AtlasRH.repository.EmployeeRepository;
import com.example.AtlasRH.service.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(
            DepartmentRepository departmentRepository,
            EmployeeRepository employeeRepository,
            DepartmentMapper departmentMapper
    ) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public DepartmentResponseDto createDepartment(DepartmentRequestDto request) {
        log.info("Création d'un nouveau département : {}", request.getName());

        if (departmentRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ResourceAlreadyExistsException(
                    "Un département portant le nom '" + request.getName().trim() + "' existe déjà."
            );
        }

        Department department = departmentMapper.toEntity(request);
        department.setName(request.getName().trim());
        Department savedDepartment = departmentRepository.save(department);

        log.info("Département créé avec succès avec l'identifiant {}", savedDepartment.getId());
        return departmentMapper.toResponseDto(savedDepartment);
    }

    @Override
    public DepartmentResponseDto updateDepartment(Long id, DepartmentRequestDto request) {
        log.info("Mise à jour du département id={}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département", "id", id));

        String newName = request.getName().trim();
        if (departmentRepository.existsByNameIgnoreCaseAndIdNot(newName, id)) {
            throw new ResourceAlreadyExistsException(
                    "Un département portant le nom '" + newName + "' existe déjà."
            );
        }

        departmentMapper.updateEntityFromDto(request, department);
        department.setName(newName);
        Department updatedDepartment = departmentRepository.save(department);

        log.info("Département id={} mis à jour avec succès", id);
        return departmentMapper.toResponseDto(updatedDepartment);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDto getDepartmentById(Long id) {
        log.info("Consultation du département id={}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département", "id", id));
        return departmentMapper.toResponseDto(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDto> getAllDepartments() {
        log.info("Récupération de la liste des départements");
        return departmentRepository.findAll()
                .stream()
                .map(departmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDepartment(Long id) {
        log.info("Suppression du département id={}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département", "id", id));

        long employeeCount = employeeRepository.countByDepartmentId(id);
        if (employeeCount > 0) {
            throw new BusinessRuleException(
                    "Un département qui contient des employés ne peut pas être supprimé. " +
                            "Nombre d'employés rattachés : " + employeeCount,
                    HttpStatus.CONFLICT
            );
        }

        departmentRepository.delete(department);
        log.info("Département id={} supprimé avec succès", id);
    }
}
