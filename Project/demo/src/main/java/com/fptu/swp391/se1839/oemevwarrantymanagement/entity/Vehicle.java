package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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
@Table(name = "Vehicle")
public class Vehicle {

    @Id
    @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
    @Column(length = 17, nullable = false, unique = true)
    String vin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    @JoinColumn(name = "modelId", nullable = false)
    Model model;

    @Min(value = 1886, message = "Product year must be >= 1886")
    @Column(nullable = false)
    int productYear;

    @Column(nullable = true, unique = true)
    @Pattern(regexp = "^[0-9]{2}[A-Z]-[0-9]{3}.[0-9]{2}$", message = "Invalid license plate format (e.g., 30A-123.45)")
    private String licensePlate;

    @Column(nullable = false)
    @Default
    LocalDate purchaseDate = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JsonIgnore
    @JoinColumn(name = "customerId")
    Customer customer;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    Set<CampaignVehicle> campaignVehicles = new HashSet<>();

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    Set<VehiclePart> vehicleParts = new HashSet<>();

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    Set<WarrantyClaim> warrantyClaims = new HashSet<>();

    @Column(name = "product_date")
    LocalDate productionDate;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Vehicle))
            return false;
        Vehicle vehicle = (Vehicle) o;
        return vin != null && vin.equals(vehicle.vin);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(vin);
    }

    @Override
    public String toString() {
        return "Vehicle [vin=" + vin + ", model=" + model + ", productYear=" + productYear + ", purchaseDate="
                + purchaseDate + ", customer=" + customer + ", campaignVehicles=" + campaignVehicles + ", vehiclePart="
                + vehicleParts.size() + ", warrantyClaim=" + warrantyClaims.size() + "]";
    }
}
