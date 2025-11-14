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
@Table(name = "RecallProgress")
public class RecallProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private RecallCampaign campaign;

    @ManyToOne
    @JoinColumn(name = "vehicle_vin")
    private Vehicle vehicle;

    @Column(length = 20, nullable = false)
    private String status; // NOTIFIED, SCHEDULED, COMPLETED, UNREACHABLE

    private LocalDateTime notificationDate;

    private LocalDateTime completionDate;

    @Column(columnDefinition = "TEXT")
    private String notes;
}