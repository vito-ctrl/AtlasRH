package com.example.AtlasRH.service;

import com.example.AtlasRH.dto.department.DepartmentRequestDto;
import com.example.AtlasRH.dto.department.DepartmentResponseDto;
import com.example.AtlasRH.entity.Department;
import com.example.AtlasRH.mapper.DepartmentMapper;
import com.example.AtlasRH.repository.DepartmentRepository;
import com.example.AtlasRH.repository.EmployeeRepository;
import com.example.AtlasRH.service.impl.DepartmentServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @Test
    void createDepartmentShouldTrimNameAndSaveIt() {

        // Arrange
        DepartmentRequestDto request =
                new DepartmentRequestDto(" Engineering ", "Product team");

        Department department = new Department();

        DepartmentResponseDto expected =
                new DepartmentResponseDto(1L, "Engineering", "Product team", 0);

        when(departmentRepository.existsByNameIgnoreCase("Engineering"))
                .thenReturn(false);

        when(departmentMapper.toEntity(request))
                .thenReturn(department);

        when(departmentRepository.save(department))
                .thenReturn(department);

        when(departmentMapper.toResponseDto(department))
                .thenReturn(expected);

        // Act
        DepartmentResponseDto result =
                departmentService.createDepartment(request);

        // Assert
        assertSame(expected, result);
        assertEquals("Engineering", department.getName());

        verify(departmentRepository).save(department);
    }
}