package com.example.AtlasRH.controller;

import com.example.AtlasRH.dto.department.DepartmentRequestDto;
import com.example.AtlasRH.dto.department.DepartmentResponseDto;
import com.example.AtlasRH.service.DepartmentService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;
    
    @ImportAutoConfiguration(exclude = {
        JpaRepositoriesAutoConfiguration.class
        })

    @Test
    void createDepartmentShouldReturn201() throws Exception {

        // Arrange
        DepartmentResponseDto response =
                new DepartmentResponseDto(
                        1L,
                        "Engineering",
                        "Product team",
                        0
                );

        when(departmentService.createDepartment(any(DepartmentRequestDto.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Engineering",
                                    "description": "Product team"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Engineering"))
                .andExpect(jsonPath("$.employeeCount").value(0));

        verify(departmentService)
                .createDepartment(any(DepartmentRequestDto.class));
    }

}