package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "PartInventory")
public class PartInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "partID", nullable = false)
    Part part;

    @Column(nullable = false)
    long quantity;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "serviceCenterId", nullable = false)
    ServiceCenter serviceCenter;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PartInventory))
            return false;
        PartInventory that = (PartInventory) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PartInventory{" + "id=" + id + ", partId=" + (part != null ? part.getId() : null) + ", serviceCenterId="
                + (serviceCenter != null ? serviceCenter.getId() : null) + '}';
    }
}
