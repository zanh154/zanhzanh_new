package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "VehiclePart")
public class VehiclePart {

    @Id
    @Column(name = "serial_number")
    String oldSerialNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    @JoinColumn(name = "vehicleVin", nullable = false)
    Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    @JoinColumn(name = "partId", nullable = false)
    Part part;

    @ManyToOne(optional = false)
    @JsonIgnore
    @JoinColumn(name = "claimId", nullable = true)
    WarrantyClaim warrantyClaim;
    LocalDate installationDate;

    LocalDate removeDate;

    @Column(name = "new_serial_number")
    String newSerialNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof VehiclePart))
            return false;
        VehiclePart that = (VehiclePart) o;
        return oldSerialNumber != null && oldSerialNumber.equals(that.oldSerialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(oldSerialNumber);
    }

    // toString
    @Override
    public String toString() {
        return "VehiclePart{" + "oldSerialNumber='" + oldSerialNumber + '\'' + ", vehicleVin="
                + (vehicle != null ? vehicle.getVin() : null) + ", partId=" + (part != null ? part.getId() : null)
                + ", claimId=" + (warrantyClaim != null ? warrantyClaim.getId() : null) + ", installationDate="
                + installationDate + ", removeDate=" + removeDate + '}';
    }
}
