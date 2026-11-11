package com.example.AtlasRH.controller;

import com.example.AtlasRH.dto.leave.LeaveTypeResponseDto;
import com.example.AtlasRH.service.LeaveTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-types")
@Tag(name = "3. Configuration - Types de congé", description = "Endpoints pour consulter les types de congé configurés (ANNUEL, EXCEPTIONNEL)")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    public LeaveTypeController(LeaveTypeService leaveTypeService) {
        this.leaveTypeService = leaveTypeService;
    }

    @GetMapping
    @Operation(summary = "Lister les types de congé", description = "Récupère les types de congés avec leur nombre de jours alloués par an.")
    @ApiResponse(responseCode = "200", description = "Liste des types de congé")
    public ResponseEntity<List<LeaveTypeResponseDto>> getAllLeaveTypes() {
        List<LeaveTypeResponseDto> types = leaveTypeService.getAllLeaveTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un type de congé", description = "Récupère les détails d'un type de congé par son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Type de congé trouvé"),
            @ApiResponse(responseCode = "404", description = "Type de congé non trouvé")
    })
    public ResponseEntity<LeaveTypeResponseDto> getLeaveTypeById(@PathVariable Long id) {
        LeaveTypeResponseDto type = leaveTypeService.getLeaveTypeById(id);
        return ResponseEntity.ok(type);
    }
}
