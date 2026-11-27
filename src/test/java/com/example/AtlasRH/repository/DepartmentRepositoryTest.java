package com.example.AtlasRH.repository;

import com.example.AtlasRH.entity.Department;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void existsByNameIgnoreCaseShouldIgnoreCase() {

        // Arrange
        Department department = new Department(
                null,
                "Engineering QA Test",
                "Test department"
        );

        departmentRepository.saveAndFlush(department);

        // Act
        boolean exists = departmentRepository
                .existsByNameIgnoreCase("engineering qa test");

        // Assert
        assertTrue(exists);
    }
}