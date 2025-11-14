package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.RecallCampaignRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.RecallVehiclesRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RecallCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RecallProgressResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RecallCampaign;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RecallProgress;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ServiceCampaign;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Vehicle;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RecallCampaignRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RecallProgressRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehicleRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.RecallCampaignService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecallCampaignServiceImpl implements RecallCampaignService {

    private final RecallCampaignRepository campaignRepository;
    private final RecallProgressRepository progressRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public RecallCampaignResponse createCampaign(RecallCampaignRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        ServiceCampaign serviceCampaign = ServiceCampaign.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .produceDateFrom(request.getProduceDateFrom())
                .produceDateTo(request.getProduceDateTo())
                .code(request.getCode())
                .build();

        RecallCampaign campaign = new RecallCampaign();
        campaign.setName(serviceCampaign.getName());
        campaign.setDescription(serviceCampaign.getDescription());
        campaign.setStartDate(serviceCampaign.getStartDate());
        campaign.setEndDate(serviceCampaign.getEndDate());
        campaign.setProduceDateFrom(serviceCampaign.getProduceDateFrom());
        campaign.setProduceDateTo(serviceCampaign.getProduceDateTo());
        campaign.setCode(serviceCampaign.getCode());
        campaign.setRiskLevel(request.getRiskLevel());
        campaign.setDefectDescription(request.getDefectDescription());
        campaign.setRemedyPlan(request.getRemedyPlan());
        campaign.setEstimatedPartsNeeded(request.getEstimatedPartsNeeded());
        campaign.setEstimatedCostPerVehicle(request.getEstimatedCostPerVehicle());

        campaign = campaignRepository.save(campaign);

        return mapToResponse(campaign);
    }

    @Override
    @Transactional
    public RecallCampaignResponse updateCampaign(Long campaignId, RecallCampaignRequest request) {
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }

        RecallCampaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Recall campaign not found"));

        campaign.setName(request.getName());
        campaign.setDescription(request.getDescription());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());
        campaign.setProduceDateFrom(request.getProduceDateFrom());
        campaign.setProduceDateTo(request.getProduceDateTo());
        campaign.setCode(request.getCode());
        campaign.setRiskLevel(request.getRiskLevel());
        campaign.setDefectDescription(request.getDefectDescription());
        campaign.setRemedyPlan(request.getRemedyPlan());
        campaign.setEstimatedPartsNeeded(request.getEstimatedPartsNeeded());
        campaign.setEstimatedCostPerVehicle(request.getEstimatedCostPerVehicle());

        campaign = campaignRepository.save(campaign);

        return mapToResponse(campaign);
    }

    @Override
    @Transactional
    public List<RecallProgressResponse> addVehiclesToCampaign(Long campaignId, RecallVehiclesRequest request) {
        RecallCampaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Recall campaign not found"));

        List<RecallProgress> newProgresses = new ArrayList<>();

        for (String vin : request.getVehicleVins()) {
            Vehicle vehicle = vehicleRepository.findById(vin)
                    .orElseThrow(() -> new EntityNotFoundException("Vehicle not found: " + vin));

            if (progressRepository.findByCampaign_IdAndVehicle_Vin(campaignId, vin).isEmpty()) {
                RecallProgress progress = RecallProgress.builder()
                        .campaign(campaign)
                        .vehicle(vehicle)
                        .status("NOTIFIED")
                        .notificationDate(LocalDateTime.now())
                        .build();

                newProgresses.add(progress);
            }
        }

        List<RecallProgress> savedProgresses = progressRepository.saveAll(newProgresses);

        return savedProgresses.stream()
                .map(this::mapToProgressResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RecallProgressResponse updateVehicleStatus(Long campaignId, String vin, String status, String notes) {
        RecallProgress progress = progressRepository.findByCampaign_IdAndVehicle_Vin(campaignId, vin)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found in campaign"));

        progress.setStatus(status);
        progress.setNotes(notes);

        if (status.equals("COMPLETED")) {
            progress.setCompletionDate(LocalDateTime.now());
        }

        progress = progressRepository.save(progress);

        return mapToProgressResponse(progress);
    }

    @Override
    public List<RecallCampaignResponse> getAllCampaigns() {
        return campaignRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RecallCampaignResponse getCampaignById(Long campaignId) {
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }

        RecallCampaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Recall campaign not found"));

        return mapToResponse(campaign);
    }

    @Override
    public List<RecallProgressResponse> getVehiclesInCampaign(Long campaignId) {
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }

        return progressRepository.findByCampaignId(campaignId).stream()
                .map(this::mapToProgressResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecallProgressResponse> getVehiclesByStatus(Long campaignId, String status) {
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        return progressRepository.findByCampaignIdAndStatus(campaignId, status).stream()
                .map(this::mapToProgressResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCampaign(Long campaignId) {
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign ID cannot be null");
        }

        if (!campaignRepository.existsById(campaignId)) {
            throw new EntityNotFoundException("Recall campaign not found");
        }

        campaignRepository.deleteById(campaignId);
    }

    private RecallCampaignResponse mapToResponse(RecallCampaign campaign) {
        List<RecallProgress> progresses = progressRepository.findByCampaignId(campaign.getId());

        // Calculate statistics
        int totalVehicles = progresses.size();
        Map<String, Integer> progressSummary = new HashMap<>();
        int completedCount = 0;

        for (RecallProgress progress : progresses) {
            String status = progress.getStatus();
            progressSummary.put(status, progressSummary.getOrDefault(status, 0) + 1);

            if (status.equals("COMPLETED")) {
                completedCount++;
            }
        }

        double completionRate = totalVehicles > 0 ? (double) completedCount / totalVehicles : 0.0;
        double estimatedTotalCost = campaign.getEstimatedCostPerVehicle() * totalVehicles;

        return RecallCampaignResponse.builder()
                .id(campaign.getId())
                .name(campaign.getName())
                .description(campaign.getDescription())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .produceDateFrom(campaign.getProduceDateFrom())
                .produceDateTo(campaign.getProduceDateTo())
                .code(campaign.getCode())
                .riskLevel(campaign.getRiskLevel())
                .defectDescription(campaign.getDefectDescription())
                .remedyPlan(campaign.getRemedyPlan())
                .estimatedPartsNeeded(campaign.getEstimatedPartsNeeded())
                .estimatedCostPerVehicle(campaign.getEstimatedCostPerVehicle())
                .totalVehicles(totalVehicles)
                .progressSummary(progressSummary)
                .completionRate(completionRate)
                .estimatedTotalCost(estimatedTotalCost)
                .build();
    }

    private RecallProgressResponse mapToProgressResponse(RecallProgress progress) {
        Vehicle vehicle = progress.getVehicle();

        return RecallProgressResponse.builder()
                .id(progress.getId())
                .campaignId(progress.getCampaign().getId())
                .campaignName(progress.getCampaign().getName())
                .campaignCode(progress.getCampaign().getCode())
                .vehicleVin(vehicle.getVin())
                .vehicleModel(vehicle.getModel().getName()) // Get the name of the Model entity
                .customerName(vehicle.getCustomer().getName()) // Changed from getFullname to getName
                .customerPhone(vehicle.getCustomer().getPhoneNumber()) // Changed from getPhone to getPhoneNumber
                .status(progress.getStatus())
                .notificationDate(progress.getNotificationDate())
                .completionDate(progress.getCompletionDate())
                .notes(progress.getNotes())
                .build();
    }
}