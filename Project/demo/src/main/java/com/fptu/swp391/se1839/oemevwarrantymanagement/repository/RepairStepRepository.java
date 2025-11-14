package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairStep;

@Repository
public interface RepairStepRepository extends JpaRepository<RepairStep, Long> {
    List<RepairStep> findByRepairOrderId(long repairOrderId);

    long countByRepairOrderId(long repairOrderStep);

    default long countAll() {
        return count();
    }

    long countByStatus(RepairStep.StepStatus status);

    List<RepairStep> findByTitleIgnoreCaseAndStatus(String title, RepairStep.StepStatus status);

    boolean existsByRepairOrderIdAndTitle(long repairOrderId, String title);
}
