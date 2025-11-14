package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.SCExpense;

@Repository
public interface SCExpenseRepository extends JpaRepository<SCExpense, Long> {

  @Query("""
      SELECT se
      FROM SCExpense se
      JOIN se.repairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.serviceCenter.id = :serviceCenterId
        AND se.repairOrder.endDate BETWEEN :startDate AND :endDate
      """)
  List<SCExpense> findByServiceCenterAndEndDateBetween(
      @Param("serviceCenterId") Long serviceCenterId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("""
      SELECT se
      FROM SCExpense se
      JOIN se.repairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE  se.repairOrder.endDate BETWEEN :startDate AND :endDate
      """)
  List<SCExpense> findAllByEndDateBetween(@Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("""
      SELECT COALESCE(SUM(e.amount), 0)
      FROM SCExpense e
      WHERE e.serviceCenter.id = :serviceCenterId
        AND e.status = 'PAID'
      """)
  double findTotalRevenueByServiceCenter(@Param("serviceCenterId") long serviceCenterId);

  @Query("""
      SELECT COALESCE(SUM(e.amount), 0)
      FROM SCExpense e
      WHERE e.status = 'PAID'
      """)
  double findTotalRevenueAllCenters();
}
