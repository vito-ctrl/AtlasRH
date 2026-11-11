package com.example.AtlasRH.controller;

import com.example.AtlasRH.dto.leave.LeaveDecisionDto;
import com.example.AtlasRH.dto.leave.LeaveRequestCreateDto;
import com.example.AtlasRH.dto.leave.LeaveRequestResponseDto;
import com.example.AtlasRH.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
@Tag(name = "2. Manager & Employé - Demandes de congé", description = "Endpoints pour la gestion et validation des demandes de congé (US4, US5)")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @PostMapping
    @Operation(summary = "Créer une demande de congé (US5 - Employé)", description = "Crée une demande de congé calculée en jours ouvrés et vérifie le solde restant ainsi que les chevauchements.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Demande créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Solde insuffisant, dates invalides ou période sans jour ouvré"),
            @ApiResponse(responseCode = "404", description = "Employé ou type de congé non trouvé"),
            @ApiResponse(responseCode = "409", description = "Chevauchement avec une autre demande en cours")
    })
    public ResponseEntity<LeaveRequestResponseDto> createLeaveRequest(
            @Valid @RequestBody LeaveRequestCreateDto request
    ) {
        LeaveRequestResponseDto created = leaveRequestService.createLeaveRequest(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une demande de congé", description = "Récupère les détails d'une demande de congé par son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Demande trouvée"),
            @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<LeaveRequestResponseDto> getLeaveRequestById(@PathVariable Long id) {
        LeaveRequestResponseDto request = leaveRequestService.getLeaveRequestById(id);
        return ResponseEntity.ok(request);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Annuler une demande de congé (US5 - Employé)", description = "Permet à l'employé d'annuler sa propre demande tant qu'elle est au statut EN_ATTENTE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Demande annulée avec succès"),
            @ApiResponse(responseCode = "400", description = "La demande n'est plus en attente"),
            @ApiResponse(responseCode = "403", description = "Seul l'employé auteur peut annuler sa demande"),
            @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<LeaveRequestResponseDto> cancelLeaveRequest(
            @PathVariable Long id,
            @Parameter(description = "Identifiant de l'employé demandeur", required = true)
            @RequestParam Long employeeId
    ) {
        LeaveRequestResponseDto cancelled = leaveRequestService.cancelLeaveRequest(id, employeeId);
        return ResponseEntity.ok(cancelled);
    }

    @GetMapping("/manager/{managerId}/pending")
    @Operation(summary = "Voir les demandes en attente de mon équipe (US4 - Manager)", description = "Récupère la liste des demandes EN_ATTENTE déposées par les membres de l'équipe du manager.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des demandes en attente"),
            @ApiResponse(responseCode = "404", description = "Manager non trouvé")
    })
    public ResponseEntity<List<LeaveRequestResponseDto>> getPendingRequestsForManagerTeam(@PathVariable Long managerId) {
        List<LeaveRequestResponseDto> pending = leaveRequestService.getPendingRequestsForManagerTeam(managerId);
        return ResponseEntity.ok(pending);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approuver une demande de congé (US4 - Manager)", description = "Approuve une demande EN_ATTENTE avec un commentaire facultatif et déclenche une notification par e-mail.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Demande approuvée avec succès"),
            @ApiResponse(responseCode = "400", description = "La demande n'est pas au statut EN_ATTENTE"),
            @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<LeaveRequestResponseDto> approveRequest(
            @PathVariable Long id,
            @RequestBody(required = false) LeaveDecisionDto decision
    ) {
        LeaveRequestResponseDto approved = leaveRequestService.approveRequest(id, decision);
        return ResponseEntity.ok(approved);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Refuser une demande de congé (US4 - Manager)", description = "Refuse une demande EN_ATTENTE. Un motif ou commentaire est obligatoire.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Demande refusée"),
            @ApiResponse(responseCode = "400", description = "Demande non en attente ou commentaire de refus manquant"),
            @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<LeaveRequestResponseDto> rejectRequest(
            @PathVariable Long id,
            @RequestBody LeaveDecisionDto decision
    ) {
        LeaveRequestResponseDto rejected = leaveRequestService.rejectRequest(id, decision);
        return ResponseEntity.ok(rejected);
    }
}
