package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "ServiceCampaign")
@SuperBuilder
public class ServiceCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private LocalDate startDate = LocalDate.now();

    private LocalDate endDate;
    @Column(name = "produce_date_from", nullable = false)
    private LocalDate produceDateFrom;
    @Column(name = "produce_date_to", nullable = false)
    private LocalDate produceDateTo;

    @NotBlank(message = "Code cannot be blank")
    @Size(min = 3, max = 10, message = "Code must be between 3 and 10 characters")
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @OneToMany(mappedBy = "serviceCampaign", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private Set<CampaignVehicle> campaignVehicles = new HashSet<>();

    @OneToMany(mappedBy = "serviceCampaign", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private Set<WarrantyClaim> warrantyClaims = new HashSet<>();

    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateValid() {
        return endDate == null || endDate.isAfter(startDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServiceCampaign))
            return false;
        ServiceCampaign that = (ServiceCampaign) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ServiceCampaign [id=" + id + ", name=" + name + ", description=" + description + ", startDate="
                + startDate + ", endDate=" + endDate + ", campaignVehicle=" + campaignVehicles + ", warrantyClaim="
                + warrantyClaims.size() + "]";
    }
}
