package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "VehicleHandover")
public class VehicleHandover {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "repair_order_id")
    private RepairOrder repairOrder;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User handoverBy;

    @Column(nullable = false)
    private LocalDateTime handoverDate;

    @Column(columnDefinition = "TEXT")
    private String checklistDetails; // JSON của checklist kiểm tra

    @Column(columnDefinition = "TEXT")
    private String customerSignature;

    @Column(columnDefinition = "TEXT")
    private String staffSignature;

    @Column(length = 20, nullable = false)
    @Builder.Default
    private String status = "PENDING"; // PENDING, COMPLETED, REJECTED

    @Column(columnDefinition = "TEXT")
    private String rejectReason;
}