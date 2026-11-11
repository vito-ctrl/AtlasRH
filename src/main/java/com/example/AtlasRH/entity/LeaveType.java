package com.example.AtlasRH.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_types")
public class LeaveType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "default_days_per_year", nullable = false)
    private Integer defaultDaysPerYear;

    @Column(name = "description", length = 255)
    private String description;

    public LeaveType() {
    }

    public LeaveType(Long id, String code, String name, Integer defaultDaysPerYear) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.defaultDaysPerYear = defaultDaysPerYear;
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
