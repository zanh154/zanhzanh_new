package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.VehicleHandoverRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.VehicleHandoverResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairOrder;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.VehicleHandover;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairOrderRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.UserRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehicleHandoverRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.VehicleHandoverService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleHandoverServiceImpl implements VehicleHandoverService {

    private final VehicleHandoverRepository handoverRepository;
    private final RepairOrderRepository repairOrderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public VehicleHandoverResponse createHandover(VehicleHandoverRequest request, Long staffId) {
        // Validate repair order
        RepairOrder repairOrder = repairOrderRepository.findById(request.getRepairOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Repair order not found"));

        // Check if repair order is completed
        if (!repairOrder.getStatus().equals(RepairOrder.OrderStatus.COMPLETED)) {
            throw new IllegalStateException("Repair order must be completed before handover");
        }

        // Validate staff
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));

        // Create handover
        VehicleHandover handover = VehicleHandover.builder()
                .repairOrder(repairOrder)
                .handoverBy(staff)
                .handoverDate(LocalDateTime.now())
                .checklistDetails(request.getChecklistDetails())
                .customerSignature(request.getCustomerSignature())
                .staffSignature(request.getStaffSignature())
                .status("PENDING")
                .build();

        handover = handoverRepository.save(handover);

        return mapToResponse(handover);
    }

    @Override
    @Transactional
    public VehicleHandoverResponse updateStatus(Long handoverId, String status, String rejectReason) {
        VehicleHandover handover = handoverRepository.findById(handoverId)
                .orElseThrow(() -> new EntityNotFoundException("Handover not found"));

        handover.setStatus(status);
        handover.setRejectReason(rejectReason);

        handover = handoverRepository.save(handover);

        return mapToResponse(handover);
    }

    @Override
    public List<VehicleHandoverResponse> getHandoversByServiceCenter(Long serviceCenterId) {
        return handoverRepository.findByRepairOrder_ServiceCenterId(serviceCenterId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleHandoverResponse> getHandoversByStaff(Long staffId) {
        return handoverRepository.findByHandoverById(staffId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleHandoverResponse getHandoverById(Long handoverId) {
        VehicleHandover handover = handoverRepository.findById(handoverId)
                .orElseThrow(() -> new EntityNotFoundException("Handover not found"));

        return mapToResponse(handover);
    }

    @Override
    @Transactional
    public void deleteHandover(Long handoverId) {
        if (!handoverRepository.existsById(handoverId)) {
            throw new EntityNotFoundException("Handover not found");
        }
        handoverRepository.deleteById(handoverId);
    }

    private VehicleHandoverResponse mapToResponse(VehicleHandover handover) {
        RepairOrder repairOrder = handover.getRepairOrder();

        return VehicleHandoverResponse.builder()
                .id(handover.getId())
                .repairOrderId(repairOrder.getId())
                .repairOrderStatus(repairOrder.getStatus().toString())
                .handoverById(handover.getHandoverBy().getId())
                .handoverByName(handover.getHandoverBy().getName())
                .handoverDate(handover.getHandoverDate())
                .checklistDetails(handover.getChecklistDetails())
                .customerSignature(handover.getCustomerSignature())
                .staffSignature(handover.getStaffSignature())
                .status(handover.getStatus())
                .rejectReason(handover.getRejectReason())
                .vehicleVin(repairOrder.getWarrantyClaim().getVehicle().getVin())
                .vehicleModel(repairOrder.getWarrantyClaim().getVehicle().getModel().getName())
                .customerName(repairOrder.getWarrantyClaim().getVehicle().getCustomer().getName())
                .customerPhone(repairOrder.getWarrantyClaim().getVehicle().getCustomer().getPhoneNumber())
                .build();
    }
}