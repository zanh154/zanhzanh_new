package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairDetail;

@Repository
public interface RepairDetailRepository extends JpaRepository<RepairDetail, Long> {

        @Query("""
                            SELECT COUNT(rd)
                            FROM RepairDetail rd
                            JOIN rd.repairOrder ro
                            JOIN ro.warrantyClaim wc
                            WHERE wc.claimDate BETWEEN :startDate AND :endDate
                            AND (:serviceCenterId IS NULL OR wc.serviceCenter.id = :serviceCenterId)
                            AND rd.part.id = :partId
                        """)
        Long countByDateAndPartIdAndServiceCenterId(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("serviceCenterId") Long serviceCenterId,
                        @Param("partId") long partId);

        @Query("""
                            SELECT rd FROM RepairDetail rd
                            JOIN rd.repairOrder ro
                            WHERE ro.id = :repairOrderId
                        """)
        List<RepairDetail> findByRepairOrderId(@Param("repairOrderId") Long repairOrderId);

        List<RepairDetail> findByRepairOrderIdAndPartId(Long repairOrderId, Long partId);
}
