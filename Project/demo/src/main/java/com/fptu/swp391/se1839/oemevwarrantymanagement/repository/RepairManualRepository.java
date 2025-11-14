package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairManual;

import java.util.Optional;

@Repository
public interface RepairManualRepository extends JpaRepository<RepairManual, Long> {
    Optional<RepairManual> findFirstByPartIdAndModel(Long partId, String model);
}
