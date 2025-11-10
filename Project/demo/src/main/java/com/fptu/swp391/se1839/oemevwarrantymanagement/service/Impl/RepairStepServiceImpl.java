package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.ChangeStatusRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ChangeStatusRepairStepResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetRepairStepResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Part;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartInventory;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairDetail;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairOrder;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairStep;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Vehicle;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.VehiclePart;
import com.fptu.swp391.se1839.oemevwarrantymanagement.event.EntityUpdatedEvent;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartInventoryRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairDetailRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairOrderRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairStepRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehiclePartRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.WarrantyClaimRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.RepairStepService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairStepServiceImpl implements RepairStepService {
    final RepairStepRepository repairStepRepository;
    final RepairOrderRepository repairOrderReposity;
    final WarrantyClaimRepository warrantyClaimRepository;
    final ApplicationEventPublisher applicationEventPublisher;
    final VehiclePartRepository vehiclePartRepository;
    final PartInventoryRepository partInventoryRepository;
    final RepairDetailRepository repairDetailRepository;

    // --- Tính % hoàn thành tất cả step ---
    int calculatePercent(long repairOrderId) {
        List<RepairStep> steps = repairStepRepository.findByRepairOrderId(repairOrderId);
        if (steps.isEmpty())
            return 0;
        long completedCount = steps.stream()
                .filter(s -> s.getStatus() == RepairStep.StepStatus.COMPLETED)
                .count();
        return (int) Math.ceil(((double) completedCount / steps.size()) * 100);
    }

    // --- Lấy các trạng thái tiếp theo có thể chọn ---
    Set<String> getNextStepStatuses(RepairStep step) {
        Set<String> nextStatuses = new LinkedHashSet<>();
        switch (step.getStatus()) {
            case PENDING -> nextStatuses.addAll(List.of("COMPLETED"));
            case WAITING -> nextStatuses.addAll(List.of("COMPLETED"));
            default -> {
            } // COMPLETED hoặc CANCELLED -> không đổi
        }
        return nextStatuses;
    }

    // --- Lấy danh sách RepairStep ---
    public List<GetRepairStepResponse> handleGetRepairStep(long repairOrderId) {
        RepairOrder repairOrder = repairOrderReposity.findById(repairOrderId)
                .orElseThrow(() -> new NoSuchElementException("Repair order does not exist: " + repairOrderId));

        List<GetRepairStepResponse> responses = new ArrayList<>();

        if (repairOrder.getTechnical() != null) {
            List<RepairStep> steps = repairStepRepository.findByRepairOrderId(repairOrder.getId())
                    .stream()
                    .filter(step -> step.getStatus() != RepairStep.StepStatus.REJECTED)
                    .toList();

            // Separate the last 2 steps
            List<RepairStep> finalSteps = steps.stream()
                    .filter(step -> step.getTitle().contains("Operation Check")
                            || step.getTitle().contains("Repair Completion"))
                    .toList();

            // Other steps (sorted by id)
            List<RepairStep> otherSteps = steps.stream()
                    .filter(step -> !finalSteps.contains(step))
                    .sorted(Comparator.comparing(RepairStep::getId))
                    .toList();

            int stepNumber = 1;

            // Add the other steps
            for (RepairStep step : otherSteps) {
                responses.add(GetRepairStepResponse.builder()
                        .stepId(step.getId())
                        .title("Step " + stepNumber + ": " + step.getTitle())
                        .estimatedHour(step.getEstimatedHours() != null ? step.getEstimatedHours() : 0.0)
                        .status(step.getStatus().name())
                        .nextStatuses(getNextStepStatuses(step))
                        .build());
                stepNumber++;
            }

            // Add the last 2 steps
            for (RepairStep step : finalSteps) {
                responses.add(GetRepairStepResponse.builder()
                        .stepId(step.getId())
                        .title("Step " + stepNumber + ": " + step.getTitle())
                        .estimatedHour(step.getEstimatedHours() != null ? step.getEstimatedHours() : 0.0)
                        .status(step.getStatus().name())
                        .nextStatuses(getNextStepStatuses(step))
                        .build());
                stepNumber++;
            }
        }

        return responses;
    }

    @Override
    @Transactional
    public ChangeStatusRepairStepResponse changeStepStatus(long repairStepId, ChangeStatusRequest newStatusStr) {
        RepairStep step = repairStepRepository.findById(repairStepId)
                .orElseThrow(() -> new NoSuchElementException("Repair step not found: " + repairStepId));

        RepairOrder order = step.getRepairOrder();
        LocalDateTime now = LocalDateTime.now();

        // Chuyển trạng thái step
        RepairStep.StepStatus newStatus;
        try {
            newStatus = RepairStep.StepStatus.valueOf(newStatusStr.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + newStatusStr.getStatus());
        }

        // Kiểm tra trạng thái hợp lệ
        Set<String> allowedStatuses = getNextStepStatuses(step);
        if (!allowedStatuses.contains(newStatus.name())) {
            throw new IllegalArgumentException("Cannot change status from " + step.getStatus() + " to " + newStatus);
        }

        step.setStatus(newStatus);
        if ("Inspection".equalsIgnoreCase(step.getTitle()) && order.getStartDate() == null) {
            order.setStartDate(LocalDateTime.now());
        }

        repairStepRepository.save(step);
        repairOrderReposity.save(order);

        // --- Logic riêng cho Disassembly và Assembly ---
        if (newStatus == RepairStep.StepStatus.COMPLETED) {
            Set<RepairDetail> details = order.getRepairDetails();
            LocalDate nowDate = LocalDate.now();

            if ("Repair/Replace Part".equalsIgnoreCase(step.getTitle())) {
                for (RepairDetail rd : details) {
                    // 1️⃣ Set status REPLACED
                    rd.setStatus(RepairDetail.DetailStatus.REPLACED);
                    repairDetailRepository.save(rd);

                    // 2️⃣ Nếu có VehiclePart → set removeDate
                    VehiclePart vp = rd.getVehiclePart();
                    if (vp != null) {
                        vp.setRemoveDate(nowDate);
                        vehiclePartRepository.save(vp);
                    }
                }
            }

            if ("Assembly".equalsIgnoreCase(step.getTitle())) {
                for (RepairDetail rd : details) {
                    VehiclePart oldVP = rd.getVehiclePart();
                    if (oldVP != null) {
                        Vehicle vehicle = oldVP.getVehicle(); // lấy từ VehiclePart cũ
                        Part part = oldVP.getPart();
                        String serialNumber = "SC-" + vehicle.getVin() + "-" + part.getId() + "-"
                                + System.currentTimeMillis();
                        oldVP.setNewSerialNumber(serialNumber);
                        vehiclePartRepository.save(oldVP);
                        VehiclePart newVP = VehiclePart.builder()
                                .vehicle(oldVP.getVehicle())
                                .part(oldVP.getPart())
                                .oldSerialNumber(serialNumber)
                                .installationDate(now.toLocalDate())
                                .build();
                        vehiclePartRepository.save(newVP);

                        PartInventory inventory = partInventoryRepository
                                .findByPartIdAndServiceCenterId(oldVP.getPart().getId(),
                                        order.getWarrantyClaim().getServiceCenter().getId())
                                .orElseThrow(() -> new RuntimeException("Inventory not found"));

                        long totalQty = order.getWarrantyClaim().getPartClaims().stream()
                                .filter(pc -> pc.getPart().getId().equals(oldVP.getPart().getId()))
                                .mapToLong(pc -> pc.getQuantity())
                                .sum();

                        System.out.println("Trừ số lượng: " + totalQty);

                        long newQuantity = inventory.getQuantity() - totalQty;
                        if (newQuantity < 0) {
                            throw new IllegalStateException(
                                    "Not enough inventory for part ID: " + oldVP.getPart().getId());
                        }
                        inventory.setQuantity(newQuantity);

                        // Lưu lại kho
                        partInventoryRepository.save(inventory);
                    }
                }
            }
        }

        syncRepairOrderStatus(order);

        int percent = calculateCompletionPercent(order);

        return ChangeStatusRepairStepResponse.builder()
                .id(step.getId())
                .status(step.getStatus().name())
                .percent(percent)
                .build();
    }

    // --- Đồng bộ trạng thái RepairOrder dựa trên step ---
    private void syncRepairOrderStatus(RepairOrder order) {
        List<RepairStep> steps = repairStepRepository.findByRepairOrderId(order.getId());

        if (steps.isEmpty()) {
            order.setStatus(RepairOrder.OrderStatus.WAITING);
            repairOrderReposity.save(order);
            return;
        }

        boolean allCompleted = steps.stream()
                .allMatch(s -> s.getStatus() == RepairStep.StepStatus.COMPLETED);
        boolean anyPending = steps.stream()
                .anyMatch(s -> s.getStatus() == RepairStep.StepStatus.PENDING);
        boolean allWaiting = steps.stream()
                .allMatch(s -> s.getStatus() == RepairStep.StepStatus.WAITING);
        boolean allCancelled = steps.stream()
                .allMatch(s -> s.getStatus() == RepairStep.StepStatus.CANCELLED);

        if (allCompleted) {
            if (!order.getSupervisorApproved()) {
                order.setStatus(RepairOrder.OrderStatus.PENDING_SUPERVISOR);
            } else {
                order.setStatus(RepairOrder.OrderStatus.COMPLETED);

                // --- Set end_date và tính end_time ---
                if (order.getStartDate() != null) {
                    LocalDateTime now = LocalDateTime.now();
                    order.setEndDate(now);

                    // Tính số giờ làm việc
                    long hours = Duration.between(order.getStartDate(), now).toHours();
                    order.setEndTime((int) hours);
                }
            }
        } else if (anyPending) {
            order.setStatus(RepairOrder.OrderStatus.PENDING);
        } else if (allWaiting) {
            order.setStatus(RepairOrder.OrderStatus.WAITING);
        } else if (allCancelled) {
            order.setStatus(RepairOrder.OrderStatus.CANCELLED);
        } else {
            order.setStatus(RepairOrder.OrderStatus.PENDING);
        }

        repairOrderReposity.save(order);
    }

    // --- Tính % hoàn thành ---
    private int calculateCompletionPercent(RepairOrder order) {
        List<RepairStep> steps = repairStepRepository.findByRepairOrderId(order.getId());
        long total = steps.size();
        long completed = steps.stream().filter(s -> s.getStatus() == RepairStep.StepStatus.COMPLETED).count();
        return total > 0 ? (int) ((completed * 100.0) / total) : 0;
    }
}