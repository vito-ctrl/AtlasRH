package com.example.AtlasRH.dto.contract;

import com.example.AtlasRH.entity.ContractType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContractRequestDto {

    @NotNull(message = "L'ID de l'employé est obligatoire")
    private Long employeeId;

    @NotNull(message = "Le type de contrat est obligatoire (CDI, CDD, STAGE)")
    private ContractType contractType;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @NotNull(message = "Le salaire brut est obligatoire")
    @Positive(message = "Le salaire brut doit être strictement supérieur à zéro")
    private BigDecimal salaireBrut;

    public ContractRequestDto() {
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public BigDecimal getSalaireBrut() {
        return salaireBrut;
    }

    public void setSalaireBrut(BigDecimal salaireBrut) {
        this.salaireBrut = salaireBrut;
    }
}
