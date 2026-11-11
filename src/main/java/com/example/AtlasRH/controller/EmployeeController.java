package com.example.AtlasRH.controller;

import com.example.AtlasRH.dto.common.PageResponseDto;
import com.example.AtlasRH.dto.contract.ContractResponseDto;
import com.example.AtlasRH.dto.employee.EmployeeHierarchyDto;
import com.example.AtlasRH.dto.employee.EmployeeRequestDto;
import com.example.AtlasRH.dto.employee.EmployeeResponseDto;
import com.example.AtlasRH.dto.leave.EmployeeLeaveBalanceDto;
import com.example.AtlasRH.dto.leave.LeaveRequestResponseDto;
import com.example.AtlasRH.entity.LeaveStatus;
import com.example.AtlasRH.service.ContractService;
import com.example.AtlasRH.service.EmployeeService;
import com.example.AtlasRH.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "1. Responsable RH - Employés", description = "Endpoints pour la gestion des employés (US2, US6, Bonus)")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final ContractService contractService;
    private final LeaveRequestService leaveRequestService;

    public EmployeeController(
            EmployeeService employeeService,
            ContractService contractService,
            LeaveRequestService leaveRequestService
    ) {
        this.employeeService = employeeService;
        this.contractService = contractService;
        this.leaveRequestService = leaveRequestService;
    }

    @PostMapping
    @Operation(summary = "Créer un employé", description = "Crée la fiche d'un employé avec matricule, nom, prénom, email, poste, date d'embauche, département et manager.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employé créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Département ou manager non trouvé"),
            @ApiResponse(responseCode = "409", description = "Matricule ou e-mail déjà existant")
    })
    public ResponseEntity<EmployeeResponseDto> createEmployee(
            @Valid @RequestBody EmployeeRequestDto request
    ) {
        EmployeeResponseDto created = employeeService.createEmployee(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une fiche employé", description = "Met à jour les informations d'un employé existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fiche employé mise à jour"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou l'employé est son propre manager"),
            @ApiResponse(responseCode = "404", description = "Employé non trouvé"),
            @ApiResponse(responseCode = "409", description = "Matricule ou e-mail en conflit")
    })
    public ResponseEntity<EmployeeResponseDto> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDto request
    ) {
        EmployeeResponseDto updated = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une fiche employé", description = "Récupère les informations complètes d'un employé.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé trouvé"),
            @ApiResponse(responseCode = "404", description = "Employé non trouvé")
    })
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable Long id) {
        EmployeeResponseDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping
    @Operation(
            summary = "Lister les employés avec pagination, tri et recherche",
            description = "Permet de paginer et filtrer les employés par nom ou département."
    )
    @ApiResponse(responseCode = "200", description = "Page d'employés")
    public ResponseEntity<PageResponseDto<EmployeeResponseDto>> getAllEmployees(
            @Parameter(description = "Recherche par nom ou prénom")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filtre par identifiant du département")
            @RequestParam(required = false) Long departmentId,
            @Parameter(description = "Filtre par nom du département")
            @RequestParam(required = false) String departmentName,
            @Parameter(description = "Numéro de la page (commence à 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri (ex: nom, dateEmbauche)")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri (asc ou desc)")
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponseDto<EmployeeResponseDto> result = employeeService.getAllEmployees(
                name, departmentId, departmentName, pageable
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/manager/{managerId}/team")
    @Operation(summary = "Consulter l'équipe d'un manager", description = "Récupère la liste des employés managés directement par un manager.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipe trouvée"),
            @ApiResponse(responseCode = "404", description = "Manager non trouvé")
    })
    public ResponseEntity<List<EmployeeResponseDto>> getTeamByManagerId(@PathVariable Long managerId) {
        List<EmployeeResponseDto> team = employeeService.getTeamByManagerId(managerId);
        return ResponseEntity.ok(team);
    }

    @GetMapping("/{id}/leave-balance")
    @Operation(summary = "Consulter le solde de congés et l'ancienneté (US6)", description = "Affiche le solde restant par type de congé et l'ancienneté calculée en années et mois.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Soldes et ancienneté"),
            @ApiResponse(responseCode = "404", description = "Employé non trouvé")
    })
    public ResponseEntity<EmployeeLeaveBalanceDto> getEmployeeLeaveBalance(
            @PathVariable Long id,
            @RequestParam(required = false) Integer year
    ) {
        EmployeeLeaveBalanceDto balance = employeeService.getEmployeeLeaveBalance(id, year);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/{id}/leave-requests")
    @Operation(summary = "Consulter mes demandes de congé (US5)", description = "Récupère les demandes de congé de l'employé avec filtre optionnel par statut.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des demandes"),
            @ApiResponse(responseCode = "404", description = "Employé non trouvé")
    })
    public ResponseEntity<List<LeaveRequestResponseDto>> getMyLeaveRequests(
            @PathVariable Long id,
            @RequestParam(required = false) LeaveStatus status
    ) {
        List<LeaveRequestResponseDto> requests = leaveRequestService.getMyLeaveRequests(id, status);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}/contracts")
    @Operation(summary = "Consulter l'historique des contrats (US3)", description = "Récupère l'ensemble des contrats passés et actuels d'un employé.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historique des contrats"),
            @ApiResponse(responseCode = "404", description = "Employé non trouvé")
    })
    public ResponseEntity<List<ContractResponseDto>> getContractsByEmployeeId(@PathVariable Long id) {
        List<ContractResponseDto> contracts = contractService.getContractsByEmployeeId(id);
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/hierarchy")
    @Operation(summary = "Organigramme de l'entreprise (Bonus)", description = "Affiche la structure hiérarchique complète de l'entreprise sous forme d'arbre.")
    @ApiResponse(responseCode = "200", description = "Arbre hiérarchique")
    public ResponseEntity<List<EmployeeHierarchyDto>> getOrganizationTree() {
        List<EmployeeHierarchyDto> hierarchy = employeeService.getOrganizationTree();
        return ResponseEntity.ok(hierarchy);
    }
}
