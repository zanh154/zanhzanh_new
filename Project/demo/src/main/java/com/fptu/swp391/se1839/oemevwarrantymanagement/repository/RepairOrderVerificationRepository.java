package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairOrderVerification;

@Repository
public interface RepairOrderVerificationRepository extends JpaRepository<RepairOrderVerification, Long> {
    Optional<RepairOrderVerification> findByRepairOrderId(Long repairOrderId);
}
