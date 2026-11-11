package com.example.AtlasRH.controller;

import com.example.AtlasRH.dto.contract.ContractRequestDto;
import com.example.AtlasRH.dto.contract.ContractResponseDto;
import com.example.AtlasRH.service.ContractService;
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
@RequestMapping("/api/contracts")
@Tag(name = "1. Responsable RH - Contrats", description = "Endpoints pour la gestion des contrats (US3)")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping
    @Operation(summary = "Créer un contrat", description = "Crée un contrat pour un employé (CDI, CDD, STAGE). Vérifie qu'il n'a pas déjà de contrat en cours et qu'une date de fin existe pour CDD/STAGE.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrat créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou date de fin manquante"),
            @ApiResponse(responseCode = "404", description = "Employé non trouvé"),
            @ApiResponse(responseCode = "409", description = "L'employé possède déjà un contrat actif")
    })
    public ResponseEntity<ContractResponseDto> createContract(
            @Valid @RequestBody ContractRequestDto request
    ) {
        ContractResponseDto created = contractService.createContract(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/terminate")
    @Operation(summary = "Terminer un contrat en cours", description = "Marque un contrat actif comme terminé et ajuste sa date de fin à aujourd'hui.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat terminé avec succès"),
            @ApiResponse(responseCode = "400", description = "Le contrat est déjà terminé"),
            @ApiResponse(responseCode = "404", description = "Contrat non trouvé")
    })
    public ResponseEntity<ContractResponseDto> terminateContract(@PathVariable Long id) {
        ContractResponseDto terminated = contractService.terminateContract(id);
        return ResponseEntity.ok(terminated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un contrat", description = "Récupère les détails d'un contrat par son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat trouvé"),
            @ApiResponse(responseCode = "404", description = "Contrat non trouvé")
    })
    public ResponseEntity<ContractResponseDto> getContractById(@PathVariable Long id) {
        ContractResponseDto contract = contractService.getContractById(id);
        return ResponseEntity.ok(contract);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Consulter l'historique des contrats d'un employé", description = "Récupère la liste chronologique des contrats d'un employé.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historique récupéré"),
            @ApiResponse(responseCode = "404", description = "Employé non trouvé")
    })
    public ResponseEntity<List<ContractResponseDto>> getContractsByEmployeeId(@PathVariable Long employeeId) {
        List<ContractResponseDto> contracts = contractService.getContractsByEmployeeId(employeeId);
        return ResponseEntity.ok(contracts);
    }
}
