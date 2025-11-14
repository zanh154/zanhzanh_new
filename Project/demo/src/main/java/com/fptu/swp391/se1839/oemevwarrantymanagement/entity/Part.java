package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@Table(name = "Part")
public class Part {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 30)
    private String partCategory;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private Set<PartPolicy> partPolicies = new HashSet<>();

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private Set<PartPriceHistory> partPriceHistories = new HashSet<>();

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private Set<PartInventory> partInventories = new HashSet<>();

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private Set<PartClaim> partClaims = new HashSet<>();

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private Set<RepairDetail> repairDetails = new HashSet<>();

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private Set<VehiclePart> vehicleParts = new HashSet<>();

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private Set<ModelPart> modelParts = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Unit unit;

    public enum Unit {
        PACK, // For battery packs
        UNIT // For other parts like motors, inverters, chargers, etc.
    }

    @NotBlank(message = "Code cannot be blank")
    @Size(min = 3, max = 10, message = "Code must be between 3 and 10 characters")
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Part))
            return false;
        Part that = (Part) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Part{id=" + id + ", name='" + name + "', partCategory='" + partCategory + "', description='"
                + description + "', partPolicyCount=" + partPolicies.size() + ", priceHistoryCount="
                + partPriceHistories.size() + ", inventoryCount=" + partInventories.size() + ", partClaimCount="
                + partClaims.size() + ", repairDetailCount=" + repairDetails.size() + ", vehiclePart="
                + vehicleParts.size() + ", modelPart=" + +modelParts.size() + "}";
    }
}