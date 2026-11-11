package com.example.AtlasRH.controller;

import com.example.AtlasRH.dto.department.DepartmentRequestDto;
import com.example.AtlasRH.dto.department.DepartmentResponseDto;
import com.example.AtlasRH.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@Tag(name = "1. Responsable RH - Départements", description = "Endpoints pour la gestion des départements (US1)")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    @Operation(summary = "Créer un département", description = "Permet de créer un nouveau département avec un nom unique.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Département créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Le nom du département existe déjà")
    })
    public ResponseEntity<DepartmentResponseDto> createDepartment(
            @Valid @RequestBody DepartmentRequestDto request
    ) {
        DepartmentResponseDto created = departmentService.createDepartment(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un département", description = "Met à jour les informations d'un département existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Département mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Département non trouvé"),
            @ApiResponse(responseCode = "409", description = "Le nom existe déjà sur un autre département")
    })
    public ResponseEntity<DepartmentResponseDto> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequestDto request
    ) {
        DepartmentResponseDto updated = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un département", description = "Récupère les détails d'un département par son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Département trouvé"),
            @ApiResponse(responseCode = "404", description = "Département non trouvé")
    })
    public ResponseEntity<DepartmentResponseDto> getDepartmentById(@PathVariable Long id) {
        DepartmentResponseDto department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    @GetMapping
    @Operation(summary = "Lister les départements", description = "Récupère l'ensemble des départements de l'entreprise.")
    @ApiResponse(responseCode = "200", description = "Liste des départements")
    public ResponseEntity<List<DepartmentResponseDto>> getAllDepartments() {
        List<DepartmentResponseDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un département", description = "Supprime un département s'il ne contient aucun employé.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Département supprimé"),
            @ApiResponse(responseCode = "404", description = "Département non trouvé"),
            @ApiResponse(responseCode = "409", description = "Suppression impossible : le département contient des employés")
    })
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
