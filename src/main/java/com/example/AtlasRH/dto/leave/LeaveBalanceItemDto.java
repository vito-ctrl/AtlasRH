package com.example.AtlasRH.dto.leave;

public class LeaveBalanceItemDto {

    private Long leaveTypeId;
    private String leaveTypeCode;
    private String leaveTypeName;
    private int totalDaysPerYear;
    private int approvedDays;
    private int remainingDays;

    public LeaveBalanceItemDto() {
    }

    public LeaveBalanceItemDto(Long leaveTypeId, String leaveTypeCode, String leaveTypeName, int totalDaysPerYear, int approvedDays, int remainingDays) {
        this.leaveTypeId = leaveTypeId;
        this.leaveTypeCode = leaveTypeCode;
        this.leaveTypeName = leaveTypeName;
        this.totalDaysPerYear = totalDaysPerYear;
        this.approvedDays = approvedDays;
        this.remainingDays = remainingDays;
    }

    public Long getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(Long leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public String getLeaveTypeCode() {
        return leaveTypeCode;
    }

    public void setLeaveTypeCode(String leaveTypeCode) {
        this.leaveTypeCode = leaveTypeCode;
    }

    public String getLeaveTypeName() {
        return leaveTypeName;
    }

    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }

    public int getTotalDaysPerYear() {
        return totalDaysPerYear;
    }

    public void setTotalDaysPerYear(int totalDaysPerYear) {
        this.totalDaysPerYear = totalDaysPerYear;
    }

    public int getApprovedDays() {
        return approvedDays;
    }

    public void setApprovedDays(int approvedDays) {
        this.approvedDays = approvedDays;
    }

    public int getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(int remainingDays) {
        this.remainingDays = remainingDays;
    }
}
