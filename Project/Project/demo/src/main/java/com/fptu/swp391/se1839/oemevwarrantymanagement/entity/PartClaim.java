package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
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
@Table(name = "PartClaim", uniqueConstraints = @UniqueConstraint(columnNames = { "partId", "warrantyClaimId" }))
public class PartClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false)
    @JsonIgnore
    @JoinColumn(name = "partId", nullable = false)
    Part part;

    @ManyToOne(optional = false)
    @JsonIgnore
    @JoinColumn(name = "warrantyClaimId", nullable = false)
    WarrantyClaim warrantyClaim;

    @Min(value = 0, message = "Quantity must be at least ")
    @Builder.Default
    long quantity = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    ClaimStatus status = ClaimStatus.PENDING;

    public enum ClaimStatus {
        PENDING, APPROVED, REJECTED
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PartClaim{id=" + id + ", partId=" + (part != null ? part.getId() : null) + ", warrantyClaimId="
                + (warrantyClaim != null ? warrantyClaim.getId() : null) + ", quantity=" + quantity + ", status="
                + status + "}";
    }
}
