package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.InspectionDetailRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.VehicleInspectionRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.InspectionDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.VehicleInspectionResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.InspectionDetail;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.VehicleInspection;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.VehiclePart;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.WarrantyClaim;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.UserRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehicleInspectionRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehiclePartRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.WarrantyClaimRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.VehicleInspectionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleInspectionServiceImpl implements VehicleInspectionService {

    private final VehicleInspectionRepository inspectionRepository;
    private final WarrantyClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final VehiclePartRepository partRepository;

    @Override
    @Transactional
    public VehicleInspectionResponse createInspection(VehicleInspectionRequest request, Long inspectorId) {
        // Validate warranty claim
        WarrantyClaim claim = claimRepository.findById(request.getClaimId())
                .orElseThrow(() -> new EntityNotFoundException("Warranty claim not found"));

        // Check if claim is in appropriate status
        if (!claim.getStatus().equals(WarrantyClaim.ClaimStatus.PENDING)) {
            throw new IllegalStateException("Warranty claim must be in PENDING status for inspection");
        }

        // Validate inspector
        User inspector = userRepository.findById(inspectorId)
                .orElseThrow(() -> new EntityNotFoundException("Inspector not found"));

        // Create inspection
        VehicleInspection inspection = VehicleInspection.builder()
                .warrantyClaim(claim)
                .inspector(inspector)
                .inspectionDate(LocalDateTime.now())
                .generalCondition(request.getGeneralCondition())
                .conclusion(request.getConclusion())
                .recommendedAction(request.getRecommendedAction())
                .build();

        // Add details
        for (InspectionDetailRequest detailRequest : request.getDetails()) {
            VehiclePart part = partRepository.findById(detailRequest.getPartId())
                    .orElseThrow(() -> new EntityNotFoundException("Part not found"));

            InspectionDetail detail = InspectionDetail.builder()
                    .inspection(inspection)
                    .part(part)
                    .condition(detailRequest.getCondition())
                    .notes(detailRequest.getNotes())
                    .evidence(detailRequest.getEvidence())
                    .build();

            inspection.getDetails().add(detail);
        }

        inspection = inspectionRepository.save(inspection);

        return mapToResponse(inspection);
    }

    @Override
    @Transactional
    public VehicleInspectionResponse updateInspection(Long inspectionId, VehicleInspectionRequest request) {
        VehicleInspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new EntityNotFoundException("Inspection not found"));

        inspection.setGeneralCondition(request.getGeneralCondition());
        inspection.setConclusion(request.getConclusion());
        inspection.setRecommendedAction(request.getRecommendedAction());

        // Update details
        inspection.getDetails().clear();
        for (InspectionDetailRequest detailRequest : request.getDetails()) {
            VehiclePart part = partRepository.findById(detailRequest.getPartId())
                    .orElseThrow(() -> new EntityNotFoundException("Part not found"));

            InspectionDetail detail = InspectionDetail.builder()
                    .inspection(inspection)
                    .part(part)
                    .condition(detailRequest.getCondition())
                    .notes(detailRequest.getNotes())
                    .evidence(detailRequest.getEvidence())
                    .build();

            inspection.getDetails().add(detail);
        }

        inspection = inspectionRepository.save(inspection);

        return mapToResponse(inspection);
    }

    @Override
    public List<VehicleInspectionResponse> getInspectionsByServiceCenter(Long serviceCenterId) {
        return inspectionRepository.findByWarrantyClaim_ServiceCenterId(serviceCenterId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleInspectionResponse> getInspectionsByInspector(Long inspectorId) {
        return inspectionRepository.findByInspectorId(inspectorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleInspectionResponse getInspectionByClaimId(Long claimId) {
        VehicleInspection inspection = inspectionRepository.findByWarrantyClaimId(claimId)
                .orElseThrow(() -> new EntityNotFoundException("Inspection not found for this claim"));

        return mapToResponse(inspection);
    }

    @Override
    public VehicleInspectionResponse getInspectionById(Long inspectionId) {
        VehicleInspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new EntityNotFoundException("Inspection not found"));

        return mapToResponse(inspection);
    }

    @Override
    @Transactional
    public void deleteInspection(Long inspectionId) {
        if (!inspectionRepository.existsById(inspectionId)) {
            throw new EntityNotFoundException("Inspection not found");
        }
        inspectionRepository.deleteById(inspectionId);
    }

    private VehicleInspectionResponse mapToResponse(VehicleInspection inspection) {
        List<InspectionDetailResponse> details = inspection.getDetails().stream()
                .map((InspectionDetail detail) -> InspectionDetailResponse.builder()
                        .id(detail.getId())
                        .partId(detail.getPart().getPart().getId())
                        .partName(detail.getPart().getPart().getName())
                        .partNumber(detail.getPart().getPart().getCode())
                        .condition(detail.getCondition())
                        .notes(detail.getNotes())
                        .evidence(detail.getEvidence())
                        .build())
                .collect(Collectors.toList());

        WarrantyClaim claim = inspection.getWarrantyClaim();

        return VehicleInspectionResponse.builder()
                .id(inspection.getId())
                .warrantyClaimId(claim.getId())
                .claimStatus(claim.getStatus().toString())
                .inspectorId(inspection.getInspector().getId())
                .inspectorName(inspection.getInspector().getName())
                .inspectionDate(inspection.getInspectionDate())
                .generalCondition(inspection.getGeneralCondition())
                .details(details)
                .conclusion(inspection.getConclusion())
                .recommendedAction(inspection.getRecommendedAction())
                .vehicleVin(claim.getVehicle().getVin())
                .vehicleModel(claim.getVehicle().getModel().getName())
                .mileage(claim.getMileage())
                .customerName(claim.getVehicle().getCustomer().getName())
                .build();
    }
}