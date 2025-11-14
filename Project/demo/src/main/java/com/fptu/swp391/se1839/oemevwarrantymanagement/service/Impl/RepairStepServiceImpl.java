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
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.AttachSerialRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.SerialDetailRequest;
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
                    .sorted(Comparator.comparing(RepairStep::getId))
                    .toList();

            int stepNumber = 1;

            // Sequential visibility rule:
            // - Always show steps that are COMPLETED
            // - Also show the first non-COMPLETED step (the current step)
            // - Hide any steps after the first non-completed step
            boolean shownFirstNonCompleted = false;

            for (RepairStep step : steps) {
                if (!shownFirstNonCompleted) {
                    responses.add(GetRepairStepResponse.builder()
                            .stepId(step.getId())
                            .title("Step " + stepNumber + ": " + step.getTitle())
                            .estimatedHour(step.getEstimatedHours() != null
                                    ? Math.round(step.getEstimatedHours() * 100.0) / 100.0
                                    : 0.0)
                            .status(step.getStatus().name())
                            .nextStatuses(getNextStepStatuses(step))
                            .build());
                    stepNumber++;

                    if (step.getStatus() != RepairStep.StepStatus.COMPLETED) {
                        // this is the first non-completed step; stop after showing it
                        shownFirstNonCompleted = true;
                    }
                } else {
                    // already shown the first non-completed step -> do not reveal further steps
                    break;
                }
            }
        }

        return responses;
    }

    @Override
    @Transactional
    public ChangeStatusRepairStepResponse completeRepairStep(long repairStepId) {
        RepairStep step = repairStepRepository.findById(repairStepId)
                .orElseThrow(() -> new NoSuchElementException("Repair step not found: " + repairStepId));

        RepairOrder order = step.getRepairOrder();
        LocalDateTime now = LocalDateTime.now();

        // Hoàn thành step
        step.setEndTime(now);
        step.setStatus(RepairStep.StepStatus.COMPLETED);

        // Tính actualHours dựa trên step trước
        List<RepairStep> stepsOrdered = order.getSteps().stream()
                .sorted(Comparator.comparingLong(RepairStep::getId))
                .collect(Collectors.toList());

        RepairStep previousStep = null;
        for (RepairStep s : stepsOrdered) {
            if (s.getId().equals(step.getId())) {
                if (previousStep == null) {
                    // Step đầu tiên → tính từ repairOrder.startDate
                    if (order.getStartDate() != null) {
                        long seconds = Duration.between(order.getStartDate(), s.getEndTime()).getSeconds();
                        s.setActualHours(seconds / 3600.0);
                    }
                } else {
                    // Các step còn lại → tính từ endTime của step trước
                    if (previousStep.getEndTime() != null) {
                        long seconds = Duration.between(previousStep.getEndTime(), s.getEndTime()).getSeconds();
                        s.setActualHours(seconds / 3600.0);
                    }
                }
                break;
            }
            previousStep = s;
        }

        repairStepRepository.save(step);

        // Tạo step tiếp theo nếu cần
        String nextStepTitle = switch (step.getTitle()) {
            case "Inspection" -> "Repair/Replace Part";
            case "Repair/Replace Part" -> "Assembly";
            case "Assembly" -> "Repair Completion";
            default -> null;
        };

        if (nextStepTitle != null) {
            boolean nextStepExists = order.getSteps().stream()
                    .anyMatch(s -> s.getTitle().equalsIgnoreCase(nextStepTitle));
            if (!nextStepExists) {
                RepairStep nextStep = RepairStep.builder()
                        .title(nextStepTitle)
                        .estimatedHours(suggestHours(nextStepTitle))
                        .status(RepairStep.StepStatus.PENDING)
                        .repairOrder(order)
                        .build();
                repairStepRepository.save(nextStep);
                order.getSteps().add(nextStep);
            }
        }

        // --- Logic Disassembly & Assembly ---
        if ("Repair/Replace Part".equalsIgnoreCase(step.getTitle())) {
            Set<RepairDetail> details = order.getRepairDetails();
            LocalDate nowDate = LocalDate.now();
            for (RepairDetail rd : details) {
                rd.setStatus(RepairDetail.DetailStatus.REPLACED);
                repairDetailRepository.save(rd);

                VehiclePart vp = rd.getVehiclePart();
                if (vp != null) {
                    vp.setRemoveDate(nowDate);
                    vehiclePartRepository.save(vp);
                }
            }
        }

        if ("Assembly".equalsIgnoreCase(step.getTitle())) {
            Set<RepairDetail> details = order.getRepairDetails();
            for (RepairDetail rd : details) {
                VehiclePart oldVP = rd.getVehiclePart();
                if (oldVP != null) {
                    Vehicle vehicle = oldVP.getVehicle();
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
                            .findByPartIdAndServiceCenterId(part.getId(),
                                    order.getWarrantyClaim().getServiceCenter().getId())
                            .orElseThrow(() -> new RuntimeException("Inventory not found"));

                    long totalQty = order.getWarrantyClaim().getPartClaims().stream()
                            .filter(pc -> pc.getPart().getId().equals(part.getId()))
                            .mapToLong(pc -> pc.getQuantity())
                            .sum();

                    long newQuantity = inventory.getQuantity() - totalQty;
                    if (newQuantity < 0)
                        throw new IllegalStateException("Not enough inventory for part ID: " + part.getId());
                    inventory.setQuantity(newQuantity);
                    partInventoryRepository.save(inventory);
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

    private double suggestHours(String title) {
        return switch (title) {
            case "Inspection" -> 0.3;
            case "Repair/Replace Part" -> 1.0;
            case "Assembly" -> 0.5;
            case "Repair Completion" -> 0.2;
            default -> 0.3;
        };
    }

    @Transactional
    public void attachNewSerialNumbers(long repairOrderId, AttachSerialRequest request) {
        RepairOrder order = repairOrderReposity.findById(repairOrderId)
                .orElseThrow(() -> new NoSuchElementException("Repair order not found"));

        LocalDateTime now = LocalDateTime.now();

        for (SerialDetailRequest item : request.getDetails()) {
            RepairDetail rd = repairDetailRepository.findById(item.getRepairDetailId())
                    .orElseThrow(
                            () -> new NoSuchElementException("Repair detail not found: " + item.getRepairDetailId()));

            VehiclePart oldVP = rd.getVehiclePart();
            if (oldVP == null)
                continue;

            // 1️⃣ Gắn số seri mới cho dòng cũ
            oldVP.setNewSerialNumber(item.getBarcode());
            oldVP.setRemoveDate(now.toLocalDate());
            vehiclePartRepository.save(oldVP);

            // 2️⃣ Tạo dòng VehiclePart mới
            Vehicle vehicle = oldVP.getVehicle();
            Part part = oldVP.getPart();

            // ⚠️ Lấy serial mới chính là "newSerialNumber" của bản cũ
            String newSerial = oldVP.getNewSerialNumber();

            VehiclePart newVP = VehiclePart.builder()
                    .oldSerialNumber(newSerial) // ID bắt buộc có – đây là serial mới
                    .vehicle(vehicle)
                    .part(part)
                    .installationDate(now.toLocalDate())
                    .build();

            vehiclePartRepository.saveAndFlush(newVP); // đảm bảo Hibernate có ID ngay

            // Cập nhật RepairDetail trỏ sang dòng mới
            rd.setStatus(RepairDetail.DetailStatus.REPLACED);
            repairDetailRepository.save(rd);

            // Cập nhật tồn kho
            PartInventory inventory = partInventoryRepository
                    .findByPartIdAndServiceCenterId(part.getId(), order.getWarrantyClaim().getServiceCenter().getId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found for part ID " + part.getId()));

            long totalQty = order.getWarrantyClaim().getPartClaims().stream()
                    .filter(pc -> pc.getPart().getId().equals(part.getId()))
                    .mapToLong(pc -> pc.getQuantity())
                    .sum();

            long newQuantity = inventory.getQuantity() - 1;
            if (newQuantity < 0) {
                throw new IllegalStateException("Not enough inventory for part ID: " + part.getId());
            }
            inventory.setQuantity(newQuantity);
            partInventoryRepository.save(inventory);

            if (newQuantity < 0)
                throw new IllegalStateException("Not enough inventory for part ID: " + part.getId());

            inventory.setQuantity(newQuantity);
            partInventoryRepository.save(inventory);
        }
    }
}