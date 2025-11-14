package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartPriceHistory;

@Repository
public interface PartPriceHistoryRepository extends JpaRepository<PartPriceHistory, Long> {

        @Query("SELECT p FROM PartPriceHistory p " +
                        "WHERE p.part.id = :partId " +
                        "AND p.startDate <= :claimDate " +
                        "AND (p.endDate IS NULL OR p.endDate >= :claimDate)")
        Optional<PartPriceHistory> findCurrentPrice(@Param("partId") Long partId,
                        @Param("claimDate") LocalDate claimDate);
}