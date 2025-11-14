package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Part;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartInventory;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ServiceCenter;

public interface PartInventoryRepository extends JpaRepository<PartInventory, Long> {

        /**
         * Lấy danh sách tồn kho của từng trung tâm.
         * Nếu serviceCenterId = null → lấy tất cả trung tâm.
         */
        @Query("""
                            SELECT pi
                            FROM PartInventory pi
                            WHERE (:serviceCenterId IS NULL OR pi.serviceCenter.id = :serviceCenterId)
                        """)
        List<PartInventory> findByServiceCenterId(@Param("serviceCenterId") Long serviceCenterId);

        /**
         * Tìm tồn kho theo ID linh kiện.
         */
        Optional<PartInventory> findByPart_Id(Long partId);

        /**
         * Đếm số lượng tồn kho theo trung tâm.
         * Nếu serviceCenterId = null → đếm tất cả.
         */
        @Query("""
                            SELECT COUNT(pi)
                            FROM PartInventory pi
                            WHERE (:serviceCenterId IS NULL OR pi.serviceCenter.id = :serviceCenterId)
                        """)
        int countByServiceCenterId(@Param("serviceCenterId") Long serviceCenterId);

        @Query("""
                            SELECT COUNT(pi)
                            FROM PartInventory pi
                        """)
        int countAllParts();

        /**
         * Tìm tồn kho theo Part và ServiceCenter.
         */
        Optional<PartInventory> findByPartAndServiceCenter(Part part, ServiceCenter serviceCenter);

        /**
         * Đếm số lượng PartInventory có quantity > 0.
         * Nếu serviceCenterId = null → đếm tất cả.
         */
        @Query("""
                            SELECT COUNT(pi)
                            FROM PartInventory pi
                            WHERE (:serviceCenterId IS NULL OR pi.serviceCenter.id = :serviceCenterId)
                              AND pi.quantity > :quantity
                        """)
        long countByServiceCenterIdAndQuantityGreaterThan(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("quantity") int quantity);

        @Query("""
                            SELECT COUNT(pi)
                            FROM PartInventory pi
                            WHERE pi.quantity > :quantity
                        """)
        long countAllPartsWithQuantityGreaterThan(@Param("quantity") int quantity);

        /**
         * Tìm tồn kho theo PartId và ServiceCenterId.
         * Nếu serviceCenterId = null → lấy tất cả có PartId đó.
         */
        @Query("""
                            SELECT pi
                            FROM PartInventory pi
                            WHERE pi.part.id = :partId
                              AND (:serviceCenterId IS NULL OR pi.serviceCenter.id = :serviceCenterId)
                        """)
        Optional<PartInventory> findByPartIdAndServiceCenterId(
                        @Param("partId") Long partId,
                        @Param("serviceCenterId") Long serviceCenterId);
}