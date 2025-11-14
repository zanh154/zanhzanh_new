package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "FailurePrediction")
public class FailurePrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "part_id")
    private VehiclePart part;

    private Integer predictedMileage;

    private Double failureProbability;

    @Column(length = 20)
    private String severity; // HIGH, MEDIUM, LOW

    @Column(columnDefinition = "TEXT")
    private String analysisData; // JSON của dữ liệu phân tích

    private LocalDateTime predictionDate;

    private LocalDateTime validUntil;
}