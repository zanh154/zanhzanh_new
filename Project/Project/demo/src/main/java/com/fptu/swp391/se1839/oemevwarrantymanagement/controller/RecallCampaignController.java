package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.RecallCampaignRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.RecallVehiclesRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RecallCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RecallProgressResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.RecallCampaignService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recall-campaigns")
@RequiredArgsConstructor
public class RecallCampaignController {

    private final RecallCampaignService recallCampaignService;

    @PostMapping
    @PreAuthorize("hasRole('EVM_STAFF')")
    public ResponseEntity<RecallCampaignResponse> createCampaign(@Valid @RequestBody RecallCampaignRequest request) {
        return ResponseEntity.ok(recallCampaignService.createCampaign(request));
    }

    @PutMapping("/{campaignId}")
    @PreAuthorize("hasRole('EVM_STAFF')")
    public ResponseEntity<RecallCampaignResponse> updateCampaign(
            @PathVariable Long campaignId,
            @Valid @RequestBody RecallCampaignRequest request) {
        return ResponseEntity.ok(recallCampaignService.updateCampaign(campaignId, request));
    }

    @PostMapping("/{campaignId}/vehicles")
    @PreAuthorize("hasRole('EVM_STAFF')")
    public ResponseEntity<List<RecallProgressResponse>> addVehiclesToCampaign(
            @PathVariable Long campaignId,
            @Valid @RequestBody RecallVehiclesRequest request) {
        return ResponseEntity.ok(recallCampaignService.addVehiclesToCampaign(campaignId, request));
    }

    @PutMapping("/{campaignId}/vehicles/{vin}")
    @PreAuthorize("hasRole('EVM_STAFF')")
    public ResponseEntity<RecallProgressResponse> updateVehicleStatus(
            @PathVariable Long campaignId,
            @PathVariable String vin,
            @RequestParam String status,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(recallCampaignService.updateVehicleStatus(campaignId, vin, status, notes));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('EVM_STAFF', 'SC_STAFF')")
    public ResponseEntity<List<RecallCampaignResponse>> getAllCampaigns() {
        return ResponseEntity.ok(recallCampaignService.getAllCampaigns());
    }

    @GetMapping("/{campaignId}")
    @PreAuthorize("hasAnyRole('EVM_STAFF', 'SC_STAFF')")
    public ResponseEntity<RecallCampaignResponse> getCampaignById(@PathVariable Long campaignId) {
        return ResponseEntity.ok(recallCampaignService.getCampaignById(campaignId));
    }

    @GetMapping("/{campaignId}/vehicles")
    @PreAuthorize("hasAnyRole('EVM_STAFF', 'SC_STAFF')")
    public ResponseEntity<List<RecallProgressResponse>> getVehiclesInCampaign(@PathVariable Long campaignId) {
        return ResponseEntity.ok(recallCampaignService.getVehiclesInCampaign(campaignId));
    }

    @GetMapping("/{campaignId}/vehicles/by-status")
    @PreAuthorize("hasAnyRole('EVM_STAFF', 'SC_STAFF')")
    public ResponseEntity<List<RecallProgressResponse>> getVehiclesByStatus(
            @PathVariable Long campaignId,
            @RequestParam String status) {
        return ResponseEntity.ok(recallCampaignService.getVehiclesByStatus(campaignId, status));
    }

    @DeleteMapping("/{campaignId}")
    @PreAuthorize("hasRole('EVM_STAFF')")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long campaignId) {
        recallCampaignService.deleteCampaign(campaignId);
        return ResponseEntity.noContent().build();
    }
}