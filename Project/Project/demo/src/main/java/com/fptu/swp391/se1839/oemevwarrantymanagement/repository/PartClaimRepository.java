package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartClaim;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.VehiclePart;

@Repository
public interface PartClaimRepository extends JpaRepository<PartClaim, Long> {

    /**
     * Đếm số PartClaim theo ServiceCenter.
     * Nếu serviceCenterId = null → lấy tất cả trung tâm.
     */
    @Query("""
                SELECT COUNT(pc)
                FROM PartClaim pc
                JOIN pc.warrantyClaim wc
                WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
            """)
    Long countByWarrantyClaimServiceCenterId(@Param("serviceCenterId") Long serviceCenterId);

    /**
     * Lấy danh sách PartClaim theo warrantyClaimId.
     */
    List<PartClaim> findByWarrantyClaimId(Long claimId);

    /**
     * Đếm lỗi (failures) theo danh mục linh kiện (category).
     * Nếu serviceCenterId = null → lấy tất cả trung tâm.
     */
    @Query("""
                SELECT p.partCategory, COUNT(pc)
                FROM PartClaim pc
                JOIN pc.warrantyClaim wc
                JOIN pc.part p
                WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                GROUP BY p.partCategory
            """)
    List<Object[]> countFailuresByCategory(@Param("serviceCenterId") Long serviceCenterId);

    @Query("""
                SELECT p.partCategory, COUNT(pc)
                FROM PartClaim pc
                JOIN pc.warrantyClaim wc
                JOIN pc.part p
                GROUP BY p.partCategory
            """)
    List<Object[]> countAllFailuresByCategory();

    /**
     * Đếm lỗi (failures) theo từng component cụ thể.
     * Nếu serviceCenterId = null → lấy tất cả trung tâm.
     */
    @Query("""
                SELECT p, COUNT(pc)
                FROM PartClaim pc
                JOIN pc.warrantyClaim wc
                JOIN pc.part p
                WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                GROUP BY p
            """)
    List<Object[]> countFailuresByComponent(@Param("serviceCenterId") Long serviceCenterId);

    @Query("""
                SELECT p, COUNT(pc)
                FROM PartClaim pc
                JOIN pc.warrantyClaim wc
                JOIN pc.part p
                GROUP BY p
            """)
    List<Object[]> countFailuresByComponentAllCenters();

    /**
     * Lấy danh sách PartClaim theo component cụ thể.
     * Nếu serviceCenterId = null → lấy tất cả trung tâm.
     */
    @Query("""
                SELECT pc
                FROM PartClaim pc
                JOIN pc.warrantyClaim wc
                JOIN pc.part p
                WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                  AND p.partCategory = :component
            """)
    List<PartClaim> findByServiceCenterAndComponent(
            @Param("serviceCenterId") Long serviceCenterId,
            @Param("component") String component);

    @Query("""
                SELECT pc
                FROM PartClaim pc
                JOIN pc.warrantyClaim wc
                JOIN pc.part p
                WHERE p.partCategory = :component
            """)
    List<PartClaim> findByComponentAllCenters(
            @Param("component") String component);

}