package com.example.AtlasRH.repository;

import com.example.AtlasRH.entity.LeaveRequest;
import com.example.AtlasRH.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeIdOrderByDateDebutDesc(Long employeeId);

    List<LeaveRequest> findByEmployeeIdAndStatusOrderByDateDebutDesc(Long employeeId, LeaveStatus status);

    List<LeaveRequest> findByEmployeeManagerIdAndStatusOrderByCreatedAtDesc(Long managerId, LeaveStatus status);

    @Query("SELECT r FROM LeaveRequest r " +
           "WHERE r.employee.id = :employeeId " +
           "AND r.status IN (com.example.AtlasRH.entity.LeaveStatus.EN_ATTENTE, com.example.AtlasRH.entity.LeaveStatus.APPROUVEE) " +
           "AND NOT (r.dateFin < :startDate OR r.dateDebut > :endDate)")
    List<LeaveRequest> findOverlappingRequests(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(r.daysCount), 0) FROM LeaveRequest r " +
           "WHERE r.employee.id = :employeeId " +
           "AND r.leaveType.id = :leaveTypeId " +
           "AND r.status = com.example.AtlasRH.entity.LeaveStatus.APPROUVEE " +
           "AND EXTRACT(YEAR FROM r.dateDebut) = :year")
    int sumApprovedDaysForYear(
            @Param("employeeId") Long employeeId,
            @Param("leaveTypeId") Long leaveTypeId,
            @Param("year") int year
    );
}
