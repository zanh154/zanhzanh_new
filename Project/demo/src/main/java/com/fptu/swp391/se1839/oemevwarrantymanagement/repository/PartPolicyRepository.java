package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartPolicy;

import jakarta.transaction.Transactional;

@Repository
public interface PartPolicyRepository extends JpaRepository<PartPolicy, Long> {

      /**
       * Lấy tất cả PartPolicy chưa hết hạn theo policyId.
       */
      @Query("""
                      SELECT p FROM PartPolicy p
                      WHERE p.warrantyPolicy.id = :policyId
                        AND p.endDate > :today
                  """)
      List<PartPolicy> findUnexpiredPartPolicies(
                  @Param("policyId") Long policyId,
                  @Param("today") LocalDate today);

      /**
       * Kiểm tra xem có bị trùng thời gian giữa Part và Policy không.
       */
      @Query("""
                      SELECT COUNT(p) > 0
                      FROM PartPolicy p
                      WHERE p.part.id = :partId
                        AND p.warrantyPolicy.id = :policyId
                        AND (
                              (:startDate BETWEEN p.startDate AND p.endDate)
                           OR (:endDate BETWEEN p.startDate AND p.endDate)
                           OR (p.startDate BETWEEN :startDate AND :endDate)
                        )
                  """)
      boolean existsByPartIdAndWarrantyPolicyIdAndDateRangeOverlap(
                  @Param("partId") Long partId,
                  @Param("policyId") Long policyId,
                  @Param("startDate") LocalDate startDate,
                  @Param("endDate") LocalDate endDate);

      /**
       * Lấy danh sách chính sách bảo hành theo Part ID.
       */
      List<PartPolicy> findByPartId(Long partId);

      /**
       * Tìm các chính sách NORMAL bị trùng thời gian.
       */
      @Query("""
                      SELECT p FROM PartPolicy p
                      WHERE p.part.id = :partId
                        AND p.warrantyPolicy.type = 'NORMAL'
                        AND (
                              (:startDate BETWEEN p.startDate AND p.endDate)
                           OR (:endDate BETWEEN p.startDate AND p.endDate)
                           OR (p.startDate BETWEEN :startDate AND :endDate)
                        )
                  """)
      List<PartPolicy> findOverlappingNormalPolicies(
                  @Param("partId") Long partId,
                  @Param("startDate") LocalDate startDate,
                  @Param("endDate") LocalDate endDate);

      /**
       * Kiểm tra xem Policy có tồn tại với trạng thái nhất định không.
       */
      boolean existsByWarrantyPolicyIdAndStatus(
                  Long warrantyPolicyId,
                  PartPolicy.Status status);

      /**
       * Lấy các PartPolicy đang ACTIVE theo Policy ID.
       */
      @Query("""
                      SELECT p FROM PartPolicy p
                      WHERE p.warrantyPolicy.id = :policyId
                        AND p.status = 'ACTIVE'
                  """)
      List<PartPolicy> findActivePartPoliciesByPolicyId(
                  @Param("policyId") Long policyId);

      /**
       * Cập nhật trạng thái cho danh sách ID.
       */
      @Modifying
      @Transactional
      @Query("""
                      UPDATE PartPolicy p
                      SET p.status = :status
                      WHERE p.id IN :ids
                  """)
      void updateStatusByIds(
                  @Param("ids") List<Long> ids,
                  @Param("status") PartPolicy.Status status);

      /**
       * Kiểm tra Policy có tồn tại hay không.
       */
      boolean existsByWarrantyPolicyId(Long policyId);

      @Query("""
                      SELECT p FROM PartPolicy p
                      WHERE p.part.id = :partId
                        AND p.status = 'ACTIVE'
                        AND p.endDate >= :today
                      ORDER BY p.endDate DESC
                      LIMIT 1
                  """)
      Optional<PartPolicy> findLatestValidPolicy(@Param("partId") Long partId, @Param("today") LocalDate today);

}
