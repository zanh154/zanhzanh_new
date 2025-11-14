package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.WarrantyClaim;

@Repository
public interface WarrantyClaimRepository extends JpaRepository<WarrantyClaim, Long> {
        @Query("SELECT COUNT(wc) FROM WarrantyClaim wc")
        long countAllClaims();

        // 🔹 Count tất cả claim, null-safe
        @Query("""
                            SELECT COUNT(wc)
                            FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                        """)
        long countByServiceCenterId(@Param("serviceCenterId") Long serviceCenterId);

        // 🔹 Count theo status
        @Query("""
                            SELECT COUNT(wc)
                            FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND wc.status = :status
                        """)
        long countByServiceCenterIdAndStatus(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("status") WarrantyClaim.ClaimStatus status);

        @Query("""
                            SELECT COUNT(wc)
                            FROM WarrantyClaim wc
                            WHERE wc.status = :status
                        """)
        long countByStatus(
                        @Param("status") WarrantyClaim.ClaimStatus status);

        // 🔹 Count theo priority
        @Query("""
                            SELECT COUNT(wc)
                            FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND wc.priority = :priority
                        """)
        long countByServiceCenterIdAndPriority(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("priority") WarrantyClaim.ClaimPriority priority);

        @Query("SELECT COUNT(wc) FROM WarrantyClaim wc WHERE wc.priority = :priority")
        long countAllByPriority(@Param("priority") WarrantyClaim.ClaimPriority priority);

        @Query("""
                           SELECT COUNT(wc)
                           FROM WarrantyClaim wc
                           WHERE wc.priority = :priority
                           AND wc.status IN :statuses
                        """)
        long countByPriorityAndStatusIn(
                        @Param("priority") WarrantyClaim.ClaimPriority priority,
                        @Param("statuses") List<WarrantyClaim.ClaimStatus> statuses);

        @Query("""
                           SELECT COUNT(wc)
                           FROM WarrantyClaim wc
                           WHERE wc.priority = :priority
                           AND wc.serviceCenter.id = :serviceCenterId
                           AND wc.status IN :statuses
                        """)
        long countByServiceCenterIdAndPriorityAndStatusIn(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("priority") WarrantyClaim.ClaimPriority priority,
                        @Param("statuses") List<WarrantyClaim.ClaimStatus> statuses);

        // 🔹 Tìm theo vehicle vin và date
        List<WarrantyClaim> findByVehicleVinAndClaimDate(String vin, LocalDate date);

        // 🔹 Tìm theo serviceCenterId, null-safe
        @Query("""
                            SELECT wc FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                        """)
        List<WarrantyClaim> findByServiceCenterId(@Param("serviceCenterId") Long serviceCenterId);

        @Query("""
                            SELECT wc FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND wc.userId = :userId
                        """)
        List<WarrantyClaim> findByServiceCenterIdAndUserId(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("userId") Long userId);

        @Query("""
                            SELECT wc FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND wc.vehicle.vin = :vin
                        """)
        List<WarrantyClaim> findByServiceCenterIdAndVehicleVin(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("vin") String vin);

        @Query("""
                            SELECT wc FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND wc.vehicle.vin = :vin
                              AND wc.userId = :userId
                        """)
        List<WarrantyClaim> findByServiceCenterIdAndVehicleVinAndUserId(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("vin") String vin,
                        @Param("userId") Long userId);

        @Query("""
                            SELECT wc FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND wc.vehicle.vin = :vin
                              AND wc.status = :status
                        """)
        List<WarrantyClaim> findByServiceCenterIdAndVehicleVinAndStatus(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("vin") String vin,
                        @Param("status") WarrantyClaim.ClaimStatus status);

        @Query("""
                            SELECT wc FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND wc.vehicle.vin = :vin
                              AND wc.status = :status
                              AND wc.userId = :userId
                        """)
        List<WarrantyClaim> findByServiceCenterIdAndVehicleVinAndStatusAndUserId(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("vin") String vin,
                        @Param("status") WarrantyClaim.ClaimStatus status,
                        @Param("userId") Long userId);

        // 🔹 Tìm theo tên khách hàng
        @Query("""
                            SELECT wc FROM WarrantyClaim wc
                            JOIN wc.vehicle v
                            JOIN v.customer c
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        """)
        List<WarrantyClaim> findByCustomerName(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("keyword") String keyword);

        @Query("""
                            SELECT wc FROM WarrantyClaim wc
                            JOIN wc.vehicle v
                            JOIN v.customer c
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND wc.userId = :userId
                              AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        """)
        List<WarrantyClaim> findByCustomerNameAndUserId(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("keyword") String keyword,
                        @Param("userId") Long userId);

        @Query("""
                            SELECT wc FROM WarrantyClaim wc
                            JOIN wc.vehicle v
                            JOIN v.customer c
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                              AND wc.status = :status
                        """)
        List<WarrantyClaim> findByServiceCenterIdAndCustomerNameAndStatus(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("keyword") String keyword,
                        @Param("status") WarrantyClaim.ClaimStatus status);

        @Query("""
                            SELECT wc FROM WarrantyClaim wc
                            JOIN wc.vehicle v
                            JOIN v.customer c
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND wc.userId = :userId
                              AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                              AND wc.status = :status
                        """)
        List<WarrantyClaim> findByServiceCenterIdAndCustomerNameAndStatusAndUserId(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("keyword") String keyword,
                        @Param("status") WarrantyClaim.ClaimStatus status,
                        @Param("userId") Long userId);

        // 🔹 Count repeat claims
        @Query("""
                            SELECT COUNT(DISTINCT wc)
                            FROM WarrantyClaim wc
                            JOIN wc.partClaims pc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND EXISTS (
                                  SELECT 1
                                  FROM WarrantyClaim c
                                  JOIN c.partClaims pc2
                                  WHERE c.vehicle.id = wc.vehicle.id
                                    AND pc2.part.id = pc.part.id
                                    AND c.id <> wc.id
                              )
                        """)
        long countRepeatClaims(@Param("serviceCenterId") Long serviceCenterId);

        @Query("""
                            SELECT COUNT(wc)
                            FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND wc.claimDate BETWEEN :startDate AND :endDate
                        """)
        long countByServiceCenterIdAndClaimDateBetween(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("""
                            SELECT COUNT(wc)
                            FROM WarrantyClaim wc
                            WHERE  wc.claimDate BETWEEN :startDate AND :endDate
                        """)
        long countByClaimDateBetween(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("""
                            SELECT COUNT(wc)
                            FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND wc.claimDate BETWEEN :startDate AND :endDate
                              AND wc.vehicle.id IN (
                                  SELECT wc2.vehicle.id
                                  FROM WarrantyClaim wc2
                                  WHERE (:serviceCenterId IS NULL OR wc2.serviceCenter.id = :serviceCenterId)
                                  GROUP BY wc2.vehicle.id
                                  HAVING COUNT(wc2.id) > 1
                              )
                        """)
        long countRepeatClaimsInRange(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // 🔹 Các báo cáo khác, null-safe
        @Query("""
                            SELECT v.model, COUNT(wc)
                            FROM WarrantyClaim wc
                            JOIN wc.vehicle v
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                            GROUP BY v.model
                        """)
        List<Object[]> countFailuresByVehicleModel(@Param("serviceCenterId") Long serviceCenterId);

        @Query("""
                            SELECT p.partCategory AS category, COUNT(wc) AS count
                            FROM WarrantyClaim wc
                            JOIN wc.partClaims pc
                            JOIN pc.part p
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                            GROUP BY p.partCategory
                        """)
        List<Object[]> countServiceCenterIdAndVehicleModel(@Param("serviceCenterId") Long serviceCenterId);

        @Query("""
                            SELECT wc.priority, COUNT(wc)
                            FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                            GROUP BY wc.priority
                        """)
        List<Object[]> countServiceCenterAndPriority(@Param("serviceCenterId") Long serviceCenterId);

        @Query("""
                            SELECT w FROM WarrantyClaim w
                            WHERE (:serviceCenterId IS NULL OR w.serviceCenter.id = :serviceCenterId)
                              AND YEAR(w.claimDate) = :year
                              AND MONTH(w.claimDate) = :month
                        """)
        List<WarrantyClaim> findByServiceCenterAndMonth(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("year") int year,
                        @Param("month") int month);

        @Query("""
                            SELECT w FROM WarrantyClaim w
                            WHERE YEAR(w.claimDate) = :year
                              AND MONTH(w.claimDate) = :month
                        """)
        List<WarrantyClaim> findByMonth(@Param("year") int year,
                        @Param("month") int month);

        @Query("""
                            SELECT c FROM WarrantyClaim c
                            JOIN c.partClaims pc
                            WHERE c.vehicle.vin = :vin
                              AND (:serviceCenterId IS NULL OR c.serviceCenter.id = :serviceCenterId)
                              AND pc.part.id = :partId
                              AND c.status NOT IN ('COMPLETED', 'CANCELLED', 'REJECTED')
                        """)
        List<WarrantyClaim> findActiveClaimsForPart(
                        @Param("vin") String vin,
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("partId") Long partId);

        @Query("""
                            SELECT COUNT(wc)
                            FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND wc.status IN :statuses
                        """)
        long countByServiceCenterIdAndStatusIn(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("statuses") WarrantyClaim.ClaimStatus statuses);

        @Query("""
                            SELECT COUNT(wc)
                            FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND wc.status IN :statuses
                        """)
        long countClaimsByServiceCenterAndStatuses(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("statuses") List<WarrantyClaim.ClaimStatus> statuses);

        @Query("""
                            SELECT COUNT(wc)
                            FROM WarrantyClaim wc
                              WHERE wc.status IN :statuses
                        """)
        long countByStatusIn(
                        @Param("statuses") WarrantyClaim.ClaimStatus statuses);

        @Query("""
                            SELECT COUNT(wc)
                            FROM WarrantyClaim wc
                              WHERE wc.status IN :statuses
                        """)
        long countClaimsByStatuses(
                        @Param("statuses") List<WarrantyClaim.ClaimStatus> statuses);

        @Query("""
                            SELECT wc
                            FROM WarrantyClaim wc
                            WHERE (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                              AND wc.status IN :statuses
                        """)
        List<WarrantyClaim> findByServiceCenterIdAndStatusIn(
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("statuses") List<WarrantyClaim.ClaimStatus> statuses);

        @Query("""
                            SELECT wc
                            FROM WarrantyClaim wc
                            WHERE wc.status IN :statuses
                        """)
        List<WarrantyClaim> findByStatusIn(
                        @Param("statuses") List<WarrantyClaim.ClaimStatus> statuses);

        List<WarrantyClaim> findByVehicleVinAndStatusNot(String vin, WarrantyClaim.ClaimStatus status);

}
