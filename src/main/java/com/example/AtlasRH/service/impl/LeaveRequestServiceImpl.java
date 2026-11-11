package com.example.AtlasRH.service.impl;

import com.example.AtlasRH.dto.leave.LeaveDecisionDto;
import com.example.AtlasRH.dto.leave.LeaveRequestCreateDto;
import com.example.AtlasRH.dto.leave.LeaveRequestResponseDto;
import com.example.AtlasRH.entity.Employee;
import com.example.AtlasRH.entity.LeaveRequest;
import com.example.AtlasRH.entity.LeaveStatus;
import com.example.AtlasRH.entity.LeaveType;
import com.example.AtlasRH.exception.BusinessRuleException;
import com.example.AtlasRH.exception.InsufficientLeaveBalanceException;
import com.example.AtlasRH.exception.ResourceNotFoundException;
import com.example.AtlasRH.mapper.LeaveRequestMapper;
import com.example.AtlasRH.repository.EmployeeRepository;
import com.example.AtlasRH.repository.LeaveRequestRepository;
import com.example.AtlasRH.repository.LeaveTypeRepository;
import com.example.AtlasRH.service.LeaveRequestService;
import com.example.AtlasRH.service.notification.EmailNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private static final Logger log = LoggerFactory.getLogger(LeaveRequestServiceImpl.class);

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestMapper leaveRequestMapper;
    private final EmailNotificationService emailNotificationService;

    public LeaveRequestServiceImpl(
            LeaveRequestRepository leaveRequestRepository,
            EmployeeRepository employeeRepository,
            LeaveTypeRepository leaveTypeRepository,
            LeaveRequestMapper leaveRequestMapper,
            EmailNotificationService emailNotificationService
    ) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveRequestMapper = leaveRequestMapper;
        this.emailNotificationService = emailNotificationService;
    }

    @Override
    public LeaveRequestResponseDto createLeaveRequest(LeaveRequestCreateDto request) {
        log.info("Création d'une demande de congé pour l'employé id={}, type={}",
                request.getEmployeeId(), request.getLeaveTypeId());

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employé", "id", request.getEmployeeId()));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Type de congé", "id", request.getLeaveTypeId()));

        // Règle 4 : La date de fin ne peut pas être avant la date de début
        if (request.getDateFin().isBefore(request.getDateDebut())) {
            throw new BusinessRuleException("La date de fin ne peut pas être antérieure à la date de début.");
        }

        // Règle 4 : Le nombre de jours est calculé en jours ouvrés (lundi à vendredi inclus)
        int workingDays = calculateWorkingDays(request.getDateDebut(), request.getDateFin());
        if (workingDays <= 0) {
            throw new BusinessRuleException("La période sélectionnée ne contient aucun jour ouvré (du lundi au vendredi).");
        }

        // Règle 4 : Deux demandes d'un même employé ne peuvent pas se chevaucher
        List<LeaveRequest> overlapping = leaveRequestRepository.findOverlappingRequests(
                employee.getId(),
                request.getDateDebut(),
                request.getDateFin()
        );
        if (!overlapping.isEmpty()) {
            throw new BusinessRuleException(
                    "Deux demandes d'un même employé ne peuvent pas se chevaucher. " +
                            "Une demande en cours existe déjà sur cette période.",
                    HttpStatus.CONFLICT
            );
        }

        // Règle 5 : Solde restant = jours par an du type – jours des demandes approuvées de l'année
        int requestYear = request.getDateDebut().getYear();
        int approvedDays = leaveRequestRepository.sumApprovedDaysForYear(
                employee.getId(),
                leaveType.getId(),
                requestYear
        );
        int remainingBalance = leaveType.getDefaultDaysPerYear() - approvedDays;

        if (workingDays > remainingBalance) {
            throw new InsufficientLeaveBalanceException(remainingBalance, workingDays);
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setDateDebut(request.getDateDebut());
        leaveRequest.setDateFin(request.getDateFin());
        leaveRequest.setDaysCount(workingDays);
        leaveRequest.setMotif(request.getMotif().trim());
        leaveRequest.setStatus(LeaveStatus.EN_ATTENTE);

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        log.info("Demande de congé id={} créée avec succès ({} jours ouvrés)", saved.getId(), workingDays);
        return leaveRequestMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getMyLeaveRequests(Long employeeId, LeaveStatus status) {
        log.info("Consultation des demandes de congé pour l'employé id={}, statut={}", employeeId, status);

        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employé", "id", employeeId);
        }

        List<LeaveRequest> requests;
        if (status != null) {
            requests = leaveRequestRepository.findByEmployeeIdAndStatusOrderByDateDebutDesc(employeeId, status);
        } else {
            requests = leaveRequestRepository.findByEmployeeIdOrderByDateDebutDesc(employeeId);
        }

        return requests.stream()
                .map(leaveRequestMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveRequestResponseDto cancelLeaveRequest(Long requestId, Long employeeId) {
        log.info("Annulation de la demande id={} par l'employé id={}", requestId, employeeId);

        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de congé", "id", requestId));

        // Règle 6 : Seul l'employé peut annuler sa demande
        if (!request.getEmployee().getId().equals(employeeId)) {
            throw new BusinessRuleException(
                    "Seul l'employé concerné est autorisé à annuler sa propre demande de congé.",
                    HttpStatus.FORBIDDEN
            );
        }

        // Règle 6 : Annulation possible uniquement si la demande est encore en attente
        if (request.getStatus() != LeaveStatus.EN_ATTENTE) {
            throw new BusinessRuleException(
                    "Une demande ne peut être annulée que si elle est encore EN_ATTENTE. " +
                            "Statut actuel : " + request.getStatus()
            );
        }

        request.setStatus(LeaveStatus.ANNULEE);
        LeaveRequest updated = leaveRequestRepository.save(request);
        log.info("Demande id={} annulée par l'employé", requestId);
        return leaveRequestMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getPendingRequestsForManagerTeam(Long managerId) {
        log.info("Consultation des demandes en attente de l'équipe du manager id={}", managerId);

        if (!employeeRepository.existsById(managerId)) {
            throw new ResourceNotFoundException("Manager", "id", managerId);
        }

        return leaveRequestRepository.findByEmployeeManagerIdAndStatusOrderByCreatedAtDesc(
                        managerId, LeaveStatus.EN_ATTENTE)
                .stream()
                .map(leaveRequestMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveRequestResponseDto approveRequest(Long requestId, LeaveDecisionDto decision) {
        log.info("Approbation de la demande id={}", requestId);

        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de congé", "id", requestId));

        // Règle 6 : Décision possible uniquement sur une demande EN_ATTENTE
        if (request.getStatus() != LeaveStatus.EN_ATTENTE) {
            throw new BusinessRuleException(
                    "Impossible de statuer sur une demande qui n'est plus EN_ATTENTE. Statut actuel : " + request.getStatus()
            );
        }

        request.setStatus(LeaveStatus.APPROUVEE);
        request.setDecisionDate(LocalDateTime.now());
        if (decision != null && decision.getComment() != null && !decision.getComment().isBlank()) {
            request.setManagerComment(decision.getComment().trim());
        }

        LeaveRequest updated = leaveRequestRepository.save(request);
        log.info("Demande id={} approuvée avec succès", requestId);

        // Bonus : envoi de l'e-mail de notification
        emailNotificationService.sendLeaveDecisionEmail(updated);

        return leaveRequestMapper.toResponseDto(updated);
    }

    @Override
    public LeaveRequestResponseDto rejectRequest(Long requestId, LeaveDecisionDto decision) {
        log.info("Refus de la demande id={}", requestId);

        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de congé", "id", requestId));

        // Règle 6 : Décision possible uniquement sur une demande EN_ATTENTE
        if (request.getStatus() != LeaveStatus.EN_ATTENTE) {
            throw new BusinessRuleException(
                    "Impossible de statuer sur une demande qui n'est plus EN_ATTENTE. Statut actuel : " + request.getStatus()
            );
        }

        // Règle 6 : Un refus exige un commentaire obligatoire
        if (decision == null || decision.getComment() == null || decision.getComment().trim().isEmpty()) {
            throw new BusinessRuleException("Un refus exige obligatoirement un commentaire ou un motif explicatif.");
        }

        request.setStatus(LeaveStatus.REFUSEE);
        request.setManagerComment(decision.getComment().trim());
        request.setDecisionDate(LocalDateTime.now());

        LeaveRequest updated = leaveRequestRepository.save(request);
        log.info("Demande id={} refusée avec le motif : {}", requestId, decision.getComment().trim());

        // Bonus : envoi de l'e-mail de notification
        emailNotificationService.sendLeaveDecisionEmail(updated);

        return leaveRequestMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveRequestResponseDto getLeaveRequestById(Long requestId) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de congé", "id", requestId));
        return leaveRequestMapper.toResponseDto(request);
    }

    /**
     * Règle 4 : Calcul du nombre de jours ouvrés (lundi à vendredi, bornes incluses).
     */
    private int calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        int workingDays = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            DayOfWeek day = currentDate.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                workingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return workingDays;
    }
}
