package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RepairStep")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;
    Double estimatedHours;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    StepStatus status = StepStatus.PENDING;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "repairOrder")
    RepairOrder repairOrder;

    String assignedTechnician;

    public enum StepStatus {
        WAITING,
        PENDING,
        COMPLETED, // Đã hoàn thành
        CANCELLED, // Bị hủy
        REJECTED // Bị từ chối
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RepairStep))
            return false;
        RepairStep that = (RepairStep) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "RepairStep{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", estimatedHours=" + estimatedHours +
                ", status=" + status +
                ", assignedTechnician='" + assignedTechnician + '\'' +
                ", repairOrderId=" + (repairOrder != null ? repairOrder.getId() : null) +
                '}';
    }
}
