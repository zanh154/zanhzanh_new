package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairOrder;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.WarrantyClaim;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {

  @Query("""
          SELECT COUNT(r.id)
          FROM RepairOrder r
          JOIN r.technical t
          WHERE t.serviceCenter.id = :serviceCenterId
            AND  r.status = :status
            AND r.endDate BETWEEN :startDate AND :endDate
      """)
  long countRepairFlWeek(@Param("serviceCenterId") Long serviceCenterId,
      @Param("status") RepairOrder.OrderStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("""
          SELECT COUNT(r.id)
          FROM RepairOrder r
          WHERE r.status = :status
            AND r.endDate BETWEEN :startDate AND :endDate
      """)
  long countRepairFlWeekAll(@Param("status") RepairOrder.OrderStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("SELECT COUNT(r.id) " +
      "FROM RepairOrder r JOIN r.technical t " +
      "WHERE t.serviceCenter.id = :serviceCenterId ")
  long countRepairFlAllStatus(@Param("serviceCenterId") Long serviceCenterId);

  @Query("SELECT COUNT(r.id) " +
      "FROM RepairOrder r JOIN r.technical t ")
  long countRepairFlAllStatusAllCenters();

  @Query("SELECT COUNT(r.id) " +
      "FROM RepairOrder r JOIN r.technical t " +
      "WHERE t.serviceCenter.id = :serviceCenterId " +
      "AND r.startDate BETWEEN :startDate AND :endDate " +
      "AND r.status = :status")
  long countRepairFlStatusAndMonth(@Param("serviceCenterId") Long serviceCenterId,
      @Param("status") RepairOrder.OrderStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("SELECT COUNT(r.id) " +
      "FROM RepairOrder r JOIN r.technical t " +
      "WHERE r.startDate BETWEEN :startDate AND :endDate " +
      "AND r.status = :status")
  long countRepairFlStatusAndMonthAll(@Param("status") RepairOrder.OrderStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.serviceCenter.id = :serviceCenterId
      AND ro.status = :status
      """)
  List<RepairOrder> findByServiceCenterIdAndStatus(@Param("serviceCenterId") Long serviceCenterId,
      @Param("status") RepairOrder.OrderStatus status);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE ro.status = :status
      """)
  List<RepairOrder> findByStatus(@Param("status") RepairOrder.OrderStatus status);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.serviceCenter.id = :serviceCenterId
      AND ro.status = :status
      AND ro.technical.id = :userId
      """)
  List<RepairOrder> findByServiceCenterIdAndStatusAndUserId(@Param("serviceCenterId") Long serviceCenterId,
      @Param("status") RepairOrder.OrderStatus status,
      @Param("userId") Long userId);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE ro.status = :status
      AND ro.technical.id = :userId
      """)
  List<RepairOrder> findByStatusAndUserId(@Param("status") RepairOrder.OrderStatus status,
      @Param("userId") Long userId);

  @Query("""
      SELECT COUNT(ro.id) FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.serviceCenter.id = :serviceCenterId
      AND ro.status = :status
      """)
  long countByServiceCenterIdAndStatus(@Param("serviceCenterId") Long serviceCenterId,
      @Param("status") RepairOrder.OrderStatus status);

  @Query("""
      SELECT COUNT(ro.id) FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE  ro.status = :status
      """)
  long countAllOrdersByStatus(@Param("status") RepairOrder.OrderStatus status);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.serviceCenter.id = :serviceCenterId
      """)
  List<RepairOrder> findByServiceCenterId(@Param("serviceCenterId") Long serviceCenterId);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.serviceCenter.id = :serviceCenterId
      AND ro.technical.id = :userId
      """)
  List<RepairOrder> findByServiceCenterIdAndUserId(@Param("serviceCenterId") Long serviceCenterId,
      @Param("userId") Long userId);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE ro.technical.id = :userId
      """)
  List<RepairOrder> findByUserId(@Param("userId") Long userId);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.serviceCenter.id = :serviceCenterId
      AND wc.vehicle.vin = :vehicleVin
      """)
  List<RepairOrder> findByServiceCenterIdAndVehicleVin(@Param("serviceCenterId") Long serviceCenterId,
      @Param("vehicleVin") String vin);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.vehicle.vin = :vehicleVin
      """)
  List<RepairOrder> findByVehicleVin(@Param("vehicleVin") String vin);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.serviceCenter.id = :serviceCenterId
      AND wc.vehicle.vin = :vehicleVin
      AND ro.technical.id = :userId
      """)
  List<RepairOrder> findByServiceCenterIdAndVehicleVinAndUserId(@Param("serviceCenterId") Long serviceCenterId,
      @Param("vehicleVin") String vin,
      @Param("userId") Long userId);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.vehicle.vin = :vehicleVin
      AND ro.technical.id = :userId
      """)
  List<RepairOrder> findByVehicleVinAndUserId(@Param("vehicleVin") String vin,
      @Param("userId") Long userId);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      JOIN wc.vehicle v
      JOIN v.customer c
      WHERE wc.serviceCenter.id = :serviceCenterId
      AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      """)
  List<RepairOrder> findByCustomerName(@Param("serviceCenterId") Long serviceCenterId,
      @Param("keyword") String name);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      JOIN wc.vehicle v
      JOIN v.customer c
      WHERE wc.serviceCenter.id = :serviceCenterId
      AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      AND ro.technical.id = :userId
      """)
  List<RepairOrder> findByServiceCenterIdAndCustomerNameAndUserId(@Param("serviceCenterId") Long serviceCenterId,
      @Param("keyword") String name,
      @Param("userId") Long userId);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      JOIN wc.vehicle v
      JOIN v.customer c
      WHERE wc.serviceCenter.id = :serviceCenterId
      AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      """)
  List<RepairOrder> findByServiceCenterIdAndCustomerName(@Param("serviceCenterId") Long serviceCenterId,
      @Param("keyword") String name);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      JOIN wc.vehicle v
      JOIN v.customer c
      WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      """)
  List<RepairOrder> findByCustomerName(@Param("keyword") String name);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      JOIN wc.vehicle v
      JOIN v.customer c
      WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      AND ro.technical.id = :userId
      """)
  List<RepairOrder> findByCustomerNameAndUserId(@Param("keyword") String name,
      @Param("userId") Long userId);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.serviceCenter.id = :serviceCenterId
      AND wc.vehicle.vin = :vehicleVin
      AND ro.status = :status
      """)
  List<RepairOrder> findByServiceCenterIdAndVehicleVinAndStatus(@Param("serviceCenterId") Long serviceCenterId,
      @Param("vehicleVin") String vin,
      @Param("status") RepairOrder.OrderStatus status);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.vehicle.vin = :vehicleVin
      AND ro.status = :status
      """)
  List<RepairOrder> findByVehicleVinAndStatus(@Param("vehicleVin") String vin,
      @Param("status") RepairOrder.OrderStatus status);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.serviceCenter.id = :serviceCenterId
      AND wc.vehicle.vin = :vehicleVin
      AND ro.status = :status
      AND ro.technical.id = :userId
      """)
  List<RepairOrder> findByServiceCenterIdAndVehicleVinAndStatusAndUserId(
      @Param("serviceCenterId") Long serviceCenterId,
      @Param("vehicleVin") String vin,
      @Param("status") RepairOrder.OrderStatus status,
      @Param("userId") Long userId);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.vehicle.vin = :vehicleVin
      AND ro.status = :status
      AND ro.technical.id = :userId
      """)
  List<RepairOrder> findByVehicleVinAndStatusAndUserId(@Param("vehicleVin") String vin,
      @Param("status") RepairOrder.OrderStatus status,
      @Param("userId") Long userId);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      JOIN wc.vehicle v
      JOIN v.customer c
      WHERE wc.serviceCenter.id = :serviceCenterId
      AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      AND ro.status = :status
      """)
  List<RepairOrder> findByServiceCenterIdAndCustomerNameAndStatus(@Param("serviceCenterId") Long serviceCenterId,
      @Param("keyword") String name,
      @Param("status") RepairOrder.OrderStatus status);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      JOIN wc.vehicle v
      JOIN v.customer c
      WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      AND ro.status = :status
      """)
  List<RepairOrder> findByCustomerNameAndStatus(@Param("keyword") String name,
      @Param("status") RepairOrder.OrderStatus status);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      JOIN wc.vehicle v
      JOIN v.customer c
      WHERE wc.serviceCenter.id = :serviceCenterId
      AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      AND ro.status = :status
      AND ro.technical.id = :userId
      """)
  List<RepairOrder> findByServiceCenterIdAndCustomerNameAndStatusAndUserId(
      @Param("serviceCenterId") Long serviceCenterId,
      @Param("keyword") String name,
      @Param("status") RepairOrder.OrderStatus status,
      @Param("userId") Long userId);

  @Query("""
      SELECT ro FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      JOIN wc.vehicle v
      JOIN v.customer c
      WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      AND ro.status = :status
      AND ro.technical.id = :userId
      """)
  List<RepairOrder> findByCustomerNameAndStatusAndUserId(@Param("keyword") String name,
      @Param("status") RepairOrder.OrderStatus status,
      @Param("userId") Long userId);

  // Các query khác giữ nguyên vì không phụ thuộc serviceCenterId
  @Query("SELECT COUNT(ro) FROM RepairOrder ro WHERE ro.technical.id = :techId AND ro.status = :status1 or ro.status = :status2")
  long countWaitingJobsByTechnician(@Param("techId") Long techId,
      @Param("status1") RepairOrder.OrderStatus status1,
      @Param("status2") RepairOrder.OrderStatus status2);

  @Query("""
      SELECT COUNT(ro) > 0
      FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE ro.id = :repairOrderId
        AND wc.serviceCenter.id = :serviceCenterId
        AND wc.status = :status
      """)
  boolean existsByRepairOrderIdAndServiceCenterIdAndStatus(@Param("repairOrderId") long repairOrderId,
      @Param("serviceCenterId") Long serviceCenterId,
      @Param("status") WarrantyClaim.ClaimStatus status);

  @Query("SELECT count(r) FROM RepairOrder r WHERE r.technical.id = :techId AND r.status IN :statuses")
  long countByTechnicalIdAndStatusIn(@Param("techId") Long techId,
      @Param("statuses") List<RepairOrder.OrderStatus> statuses);

  @Query("SELECT MAX(ro.startDate) FROM RepairOrder ro " +
      "WHERE ro.technical.id = :technicalId AND ro.status IN :statuses")
  LocalDateTime findLatestStartByTechnical(@Param("technicalId") Long technicalId,
      @Param("statuses") List<RepairOrder.OrderStatus> statuses);

  @Query("""
      SELECT count(ro)
      FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.serviceCenter.id = :serviceCenterId
      """)
  int countByServiceCenterId(@Param("serviceCenterId") Long serviceCenterId);

  @Query("""
      SELECT count(ro)
      FROM RepairOrder ro
      """)
  int countAllOrders();

  @Query("""
      SELECT count(ro)
      FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE wc.serviceCenter.id = :serviceCenterId
        AND ro.status = :status
        AND ro.endDate BETWEEN :startDate AND :endDate
      """)
  int countByServiceCenterIdAndStatusAndEndDateBetween(@Param("serviceCenterId") Long serviceCenterId,
      @Param("status") RepairOrder.OrderStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("""
      SELECT count(ro)
      FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE ro.status = :status
        AND ro.endDate BETWEEN :startDate AND :endDate
      """)
  int countByStatusAndEndDateBetween(@Param("status") RepairOrder.OrderStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("SELECT COUNT(ro) FROM RepairOrder ro " +
      "JOIN ro.warrantyClaim wc " +
      "WHERE wc.serviceCenter.id = :serviceCenterId " +
      "AND ro.startDate BETWEEN :startDate AND :endDate")
  long countByServiceCenterIdAndStartDateBetween(@Param("serviceCenterId") Long serviceCenterId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("SELECT COUNT(ro) FROM RepairOrder ro " +
      "JOIN ro.warrantyClaim wc " +
      "WHERE ro.startDate BETWEEN :startDate AND :endDate")
  long countAllOrdersBetween(@Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("SELECT COUNT(ro) FROM RepairOrder ro " +
      "JOIN ro.warrantyClaim wc " +
      "WHERE wc.serviceCenter.id = :serviceCenterId " +
      "AND ro.status = :status " +
      "AND ro.startDate BETWEEN :startDate AND :endDate")
  long countByServiceCenterIdAndStatusAndStartDateBetween(@Param("serviceCenterId") Long serviceCenterId,
      @Param("status") RepairOrder.OrderStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  @Query("SELECT COUNT(ro) FROM RepairOrder ro " +
      "JOIN ro.warrantyClaim wc " +
      "WHERE ro.status = :status " +
      "AND ro.startDate BETWEEN :startDate AND :endDate")
  long countAllOrdersByStatusBetween(@Param("status") RepairOrder.OrderStatus status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  // Các query thống kê cuối cùng giữ nguyên
  @Query("""
      SELECT wc.serviceCenter.id AS scId,
             wc.serviceCenter.name AS scName,
             COUNT(ro.id) AS totalCompletedOrder
      FROM RepairOrder ro
      JOIN ro.warrantyClaim wc
      WHERE ro.status = 'COMPLETED'
      GROUP BY wc.serviceCenter.id, wc.serviceCenter.name
      ORDER BY wc.serviceCenter.name
      """)
  List<Object[]> getCompletedOrderReport();

  @Query("""
      SELECT COUNT(ro)
      FROM RepairOrder ro
      WHERE ro.status = 'COMPLETED'
        AND FUNCTION('TIMESTAMPDIFF', HOUR, ro.startDate, ro.endDate) < 24
      """)
  long countCompletedUnder24h();

  @Query("""
      SELECT COUNT(ro)
      FROM RepairOrder ro
      WHERE ro.status = 'COMPLETED'
        AND FUNCTION('TIMESTAMPDIFF', HOUR, ro.startDate, ro.endDate) < 72
        AND FUNCTION('TIMESTAMPDIFF', HOUR, ro.startDate, ro.endDate) >= 24
      """)
  long countCompletedUnder72h();

  @Query("""
      SELECT COUNT(ro)
      FROM RepairOrder ro
      WHERE ro.status = 'COMPLETED'
        AND FUNCTION('TIMESTAMPDIFF', HOUR, ro.startDate, ro.endDate) < 168
        AND FUNCTION('TIMESTAMPDIFF', HOUR, ro.startDate, ro.endDate) >= 72
      """)
  long countCompletedUnder168h();

  @Query("""
      SELECT COUNT(ro)
      FROM RepairOrder ro
      WHERE ro.status = 'COMPLETED'
        AND FUNCTION('TIMESTAMPDIFF', HOUR, ro.startDate, ro.endDate) >= 168
      """)
  long countCompletedOver168h();

  @Query("""
      SELECT ro
      FROM RepairOrder ro
      JOIN FETCH ro.warrantyClaim wc
      LEFT JOIN FETCH ro.repairDetails rd
      LEFT JOIN FETCH rd.part p
      JOIN wc.vehicle v
      WHERE v.vin = :vin
      ORDER BY ro.startDate DESC
      """)
  List<RepairOrder> findRecentRepairOrdersByVin(@Param("vin") String vin, Pageable pageable);
}