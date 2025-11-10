package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Table(name = "WarrantyPolicy")
public class WarrantyPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Code cannot be blank")
    @Size(min = 3, max = 10, message = "Code must be between 3 and 10 characters")
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String name;

    @Min(value = 1, message = "Duration must be at least 1 month")
    @Max(value = 120, message = "Duration cannot exceed 120 months") // tối đa 10 năm
    @Column(nullable = false)
    private int durationPeriod;

    @Min(value = 0, message = "Mileage limit cannot be negative")
    @Max(value = 1000000, message = "Mileage limit cannot exceed 1,000,000 km")
    @Column(nullable = false)
    private int mileageLimit;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PolicyType type;

    @JsonManagedReference
    @OneToMany(mappedBy = "warrantyPolicy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private Set<PartPolicy> partPolicies = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status = Status.INACTIVE;

    @OneToMany(mappedBy = "warrantyPolicy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private Set<ModelPolicy> modelPolicies = new HashSet<>();

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    public enum PolicyType {
        NORMAL, // normmal policies
        PROMOTION // part-time/festival/bonus policies
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof WarrantyPolicy))
            return false;
        WarrantyPolicy that = (WarrantyPolicy) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "WarrantyPolicy{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", durationPeriod=" + durationPeriod +
                ", mileageLimit=" + mileageLimit +
                ", type=" + type +
                '}';
    }
}