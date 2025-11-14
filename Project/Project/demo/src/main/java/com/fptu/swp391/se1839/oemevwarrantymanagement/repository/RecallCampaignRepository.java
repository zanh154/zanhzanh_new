package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RecallCampaign;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RecallProgress;

@Repository
public interface RecallCampaignRepository extends JpaRepository<RecallCampaign, Long> {
    Optional<RecallProgress> findByCampaign_IdAndVehicle_Vin(Long campaignId, String vin);

    List<RecallProgress> findByCampaign_Id(Long campaignId);

    List<RecallProgress> findByCampaign_IdAndStatus(Long campaignId, String status);
}