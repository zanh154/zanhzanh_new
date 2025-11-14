package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.VehicleHandover;

public interface VehicleHandoverRepository extends JpaRepository<VehicleHandover, Long> {
    // Tìm theo repair order
    java.util.Optional<VehicleHandover> findByRepairOrderId(Long repairOrderId);

    // Tìm theo người bàn giao
    java.util.List<VehicleHandover> findByHandoverById(Long handoverById);

    // Tìm theo trạng thái
    java.util.List<VehicleHandover> findByStatus(String status);

    // Tìm theo service center của repair order
    java.util.List<VehicleHandover> findByRepairOrder_ServiceCenterId(Long serviceCenterId);
}