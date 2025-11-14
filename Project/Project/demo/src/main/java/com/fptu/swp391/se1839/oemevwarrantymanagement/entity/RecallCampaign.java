package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "RecallCampaign")
@PrimaryKeyJoinColumn(name = "campaign_id")
@SuperBuilder
public class RecallCampaign extends ServiceCampaign {

    @Column(length = 20, nullable = false)
    private String riskLevel; // HIGH, MEDIUM, LOW

    @Column(columnDefinition = "TEXT")
    private String defectDescription;

    @Column(columnDefinition = "TEXT")
    private String remedyPlan;

    private Integer estimatedPartsNeeded;

    private Double estimatedCostPerVehicle;
}