package com.example.AtlasRH.repository;

import com.example.AtlasRH.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByMatriculeIgnoreCase(String matricule);

    boolean existsByMatriculeIgnoreCaseAndIdNot(String matricule, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    long countByDepartmentId(Long departmentId);

    List<Employee> findByManagerId(Long managerId);

    List<Employee> findByManagerIsNull();

    Optional<Employee> findByMatriculeIgnoreCase(String matricule);

    Optional<Employee> findByEmailIgnoreCase(String email);

    Page<Employee> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom, Pageable pageable);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    Page<Employee> findByDepartmentNameContainingIgnoreCase(String departmentName, Pageable pageable);

    Page<Employee> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseAndDepartmentId(
            String nom, String prenom, Long departmentId, Pageable pageable);
}
