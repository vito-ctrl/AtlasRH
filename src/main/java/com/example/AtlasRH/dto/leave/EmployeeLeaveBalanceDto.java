package com.example.AtlasRH.dto.leave;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeLeaveBalanceDto {

    private Long employeeId;
    private String matricule;
    private String nom;
    private String prenom;
    private LocalDate dateEmbauche;
    private String seniority;
    private int year;
    private List<LeaveBalanceItemDto> balances = new ArrayList<>();

    public EmployeeLeaveBalanceDto() {
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public String getSeniority() {
        return seniority;
    }

    public void setSeniority(String seniority) {
        this.seniority = seniority;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<LeaveBalanceItemDto> getBalances() {
        return balances;
    }

    public void setBalances(List<LeaveBalanceItemDto> balances) {
        this.balances = balances;
    }
}
