package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.VehicleInspection;

public interface VehicleInspectionRepository extends JpaRepository<VehicleInspection, Long> {
    // Tìm theo warranty claim
    java.util.Optional<VehicleInspection> findByWarrantyClaimId(Long warrantyClaimId);

    // Tìm theo người kiểm tra
    java.util.List<VehicleInspection> findByInspectorId(Long inspectorId);

    // Tìm theo service center của warranty claim
    java.util.List<VehicleInspection> findByWarrantyClaim_ServiceCenterId(Long serviceCenterId);

    // Tìm trong khoảng thời gian
    java.util.List<VehicleInspection> findByInspectionDateBetween(java.time.LocalDateTime start,
            java.time.LocalDateTime end);
}