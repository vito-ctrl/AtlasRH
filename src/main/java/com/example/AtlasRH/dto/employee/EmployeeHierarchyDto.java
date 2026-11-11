package com.example.AtlasRH.dto.employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeHierarchyDto {

    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private String poste;
    private String departmentName;
    private List<EmployeeHierarchyDto> subordinates = new ArrayList<>();

    public EmployeeHierarchyDto() {
    }

    public EmployeeHierarchyDto(Long id, String matricule, String nom, String prenom, String poste, String departmentName) {
        this.id = id;
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.poste = poste;
        this.departmentName = departmentName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public List<EmployeeHierarchyDto> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(List<EmployeeHierarchyDto> subordinates) {
        this.subordinates = subordinates;
    }
}
