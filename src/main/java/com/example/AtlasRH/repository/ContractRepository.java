package com.example.AtlasRH.repository;

import com.example.AtlasRH.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByEmployeeIdOrderByDateDebutDesc(Long employeeId);

    Optional<Contract> findByEmployeeIdAndActifTrue(Long employeeId);

    boolean existsByEmployeeIdAndActifTrue(Long employeeId);
}
