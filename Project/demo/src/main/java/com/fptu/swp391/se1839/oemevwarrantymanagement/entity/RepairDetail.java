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
@Table(name = "RepairDetail")
public class RepairDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false)
    @JsonIgnore
    @JoinColumn(name = "repairOrderId", nullable = false)
    RepairOrder repairOrder;

    @ManyToOne(optional = false)
    @JsonIgnore
    @JoinColumn(name = "partID", nullable = false)
    Part part;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    DetailStatus status = DetailStatus.PENDING;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "vehiclePartId")
    VehiclePart vehiclePart;

    @Column(columnDefinition = "TEXT")
    String description;

    public enum DetailStatus {
        PENDING, USED, REPLACED, REJECTED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RepairDetail))
            return false;
        RepairDetail that = (RepairDetail) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "RepairDetail{" + "id=" + id + ", repairOrderId=" + (repairOrder != null ? repairOrder.getId() : null)
                + ", partId=" + (part != null ? part.getId() : null) + ", status=" + status + ", description='"
                + description + '\'' + '}';
    }
}
