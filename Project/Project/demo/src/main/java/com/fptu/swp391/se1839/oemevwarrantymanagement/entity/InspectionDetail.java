package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

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
@Table(name = "InspectionDetail")
public class InspectionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inspection_id")
    private VehicleInspection inspection;

    @ManyToOne
    @JoinColumn(name = "part_id")
    private VehiclePart part;

    @Column(length = 20, nullable = false)
    private String condition; // GOOD, DAMAGED, NEEDS_REPLACEMENT

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(length = 255)
    private String evidence; // URL to photos/videos
}