package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ModelPolicy;

public interface ModelPolicyRepository extends JpaRepository<ModelPolicy, Long> {

    @Query("""
            SELECT mp FROM ModelPolicy mp
            WHERE mp.model.id = :modelId
              AND mp.status = 'ACTIVE'
              AND :purchaseDate BETWEEN mp.effectiveDate AND COALESCE(mp.expiryDate, CURRENT_DATE)
            """)
    Optional<ModelPolicy> findActivePolicyByModelAndDate(Long modelId, LocalDate purchaseDate);
}
