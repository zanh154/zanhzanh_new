package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "WarrantyClaim")
public class WarrantyClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;


    @ManyToOne(optional = false)
    @JsonIgnore
    @JoinColumn(name = "vin", nullable = false)
    Vehicle vehicle;

    @ManyToOne(optional = true)
    @JsonIgnore
    @JoinColumn(name = "serviceCenterId", nullable = true)
    ServiceCenter serviceCenter;

    @NotNull
    @Column(nullable = false)
    @Builder.Default
    LocalDateTime claimDate = LocalDateTime.now();

    @NotNull
    @Min(0) // mileage >= 0
    @Column(nullable = false)
    int mileage;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Builder.Default
    String description = null;

    @Column(length = 1000)
    private String diagnosis;

    @Column(nullable = true)
    LocalDate decisionDate;

    public enum ClaimStatus {
        DRAFT, PENDING, APPROVED, REJECTED, COMPLETED
    }

    @Column(nullable = false)
    Long userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    ClaimStatus status = ClaimStatus.DRAFT;

    public enum ClaimPriority {
        NORMAL, HIGH, URGENT
    }

    @Column(length = 20)
    @Builder.Default
    String rejectBy = null;

    @Column(columnDefinition = "TEXT")
    String rejectReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    ClaimPriority priority = ClaimPriority.NORMAL;

    @OneToOne(mappedBy = "warrantyClaim", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    RepairOrder repairOrder;

    @OneToMany(mappedBy = "warrantyClaim", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    Set<VehiclePart> vehicleParts = new HashSet<>();

    @OneToMany(mappedBy = "warrantyClaim", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    Set<PartClaim> partClaims = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serviceCampaignId")
    ServiceCampaign serviceCampaign;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof WarrantyClaim))
            return false;
        WarrantyClaim that = (WarrantyClaim) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "WarrantyClaim{" +
                "id=" + id +
                ", vehicleVIN=" + (vehicle == null ? "null" : vehicle.getVin()) +
                ", serviceCenterId=" + (serviceCenter == null ? "null" : serviceCenter.getId()) +
                ", claimDate=" + claimDate +
                ", mileage=" + mileage +
                ", description=" + (description == null ? "null" : "'" + description + "'") +
                ", decisionDate=" + decisionDate +
                ", status=" + status +
                ", priority=" + priority +
                ", repairOrderId=" + (repairOrder == null ? "null" : repairOrder.getId()) +
                ", vehiclePartCount=" + (vehicleParts == null ? 0 : vehicleParts.size()) +
                ", serviceCampaignId=" + (serviceCampaign == null ? "null" : serviceCampaign.getId()) +
                ", partClaimCount=" + (partClaims == null ? 0 : partClaims.size()) +
                '}';
    }

}
