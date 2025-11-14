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
@Table(name = "CampaignVehicle", uniqueConstraints = @UniqueConstraint(columnNames = { "campaignID", "vin" }))
public class CampaignVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "campaignId", nullable = false)
    ServiceCampaign serviceCampaign;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "vin", nullable = false)
    Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    CampaignVehicleStatus status = CampaignVehicleStatus.NOTIFIED;

    public enum CampaignVehicleStatus {
        ACTIVE, NOTIFIED, COMPLETED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CampaignVehicle))
            return false;
        CampaignVehicle that = (CampaignVehicle) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CampaignVehicle [id=" + id + ", serviceCampaign=" + serviceCampaign + ", vehicle=" + vehicle
                + ", status=" + status + "]";
    }
}
