package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RecallProgress;

public interface RecallProgressRepository extends JpaRepository<RecallProgress, Long> {
    // Tìm theo chiến dịch recall
    List<RecallProgress> findByCampaignId(Long campaignId);

    // Tìm theo xe
    List<RecallProgress> findByVehicleVin(String vin);

    // Tìm theo trạng thái
    List<RecallProgress> findByStatus(String status);

    // Tìm theo trạng thái và chiến dịch
    List<RecallProgress> findByCampaignIdAndStatus(Long campaignId, String status);

    // Tìm chưa hoàn thành
    List<RecallProgress> findByCompletionDateIsNull();

    Optional<RecallProgress> findByCampaign_IdAndVehicle_Vin(Long campaignId, String vehicle);
}