package com.example.AtlasRH.dto.leave;

public class LeaveTypeResponseDto {

    private Long id;
    private String code;
    private String name;
    private Integer defaultDaysPerYear;
    private String description;

    public LeaveTypeResponseDto() {
    }

    public LeaveTypeResponseDto(Long id, String code, String name, Integer defaultDaysPerYear, String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.defaultDaysPerYear = defaultDaysPerYear;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDefaultDaysPerYear() {
        return defaultDaysPerYear;
    }

    public void setDefaultDaysPerYear(Integer defaultDaysPerYear) {
        this.defaultDaysPerYear = defaultDaysPerYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
