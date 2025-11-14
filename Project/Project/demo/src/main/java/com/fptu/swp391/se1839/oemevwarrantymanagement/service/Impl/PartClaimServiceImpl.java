package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.ChangeStatusPartClaimRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.PartClaimRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ClaimsByComponentResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetPartClaimResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.claimsByCategoryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Part;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartClaim;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartPolicy;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartPriceHistory;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairDetail;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairOrder;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairStep;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.WarrantyClaim;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.WarrantyPolicy;
import com.fptu.swp391.se1839.oemevwarrantymanagement.event.EntityUpdatedEvent;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartClaimRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairDetailRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairOrderRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairStepRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.UserRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.WarrantyClaimRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PartClaimService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartClaimServiceImpl implements PartClaimService {
    final PartClaimRepository partClaimRepository;
    final WarrantyClaimRepository warrantyClaimRepository;
    final RepairDetailRepository repairDetailRepository;
    final RepairStepRepository repairStepRepository;
    final RepairOrderRepository repairOrderRepository;
    final ApplicationEventPublisher eventPublisher;
    final UserRepository userRepository;

    public List<claimsByCategoryResponse> calculateClaimsByCategory(Long serviceCenterId) {
        boolean hasSpecificCenter = serviceCenterId != null && serviceCenterId > 0;

        // Tổng số claim
        Long totalClaims = hasSpecificCenter
                ? partClaimRepository.countByWarrantyClaimServiceCenterId(serviceCenterId)
                : partClaimRepository.count(); // tổng cho tất cả trung tâm
        if (totalClaims == 0)
            totalClaims = 1L; // tránh chia cho 0

        // Lấy dữ liệu theo category
        List<Object[]> categoryData = hasSpecificCenter
                ? partClaimRepository.countFailuresByCategory(serviceCenterId)
                : partClaimRepository.countAllFailuresByCategory(); // cần implement query tổng cho tất cả trung tâm

        List<claimsByCategoryResponse> claimsByCategory = new ArrayList<>();
        for (Object[] row : categoryData) {
            String category = (String) row[0];
            Long count = (Long) row[1];
            double percentage = (count * 100.0) / totalClaims;

            claimsByCategory.add(
                    claimsByCategoryResponse.builder()
                            .category(category)
                            .percentage(String.format("%.1f%%", percentage))
                            .build());
        }
        return claimsByCategory;
    }

    public List<ClaimsByComponentResponse> calculateClaimsByComponent(Long serviceCenterId) {
        Long totalClaims = partClaimRepository.countByWarrantyClaimServiceCenterId(
                serviceCenterId != null && serviceCenterId > 0 ? serviceCenterId : null);
        if (totalClaims == 0)
            totalClaims = 1L;

        List<Object[]> componentData = partClaimRepository.countFailuresByComponent(serviceCenterId);

        List<ClaimsByComponentResponse> result = new ArrayList<>();
        for (Object[] row : componentData) {
            Part part = (Part) row[0];
            Long count = (Long) row[1];
            double percentage = (count * 100.0) / totalClaims;

            result.add(ClaimsByComponentResponse.builder()
                    .component(part.getName())
                    .count(count)
                    .percentage(String.format("%.1f%%", percentage))
                    .build());
        }

        return result;
    }

    String getCoverageDescription(WarrantyPolicy policy) {
        if (policy.getType() == WarrantyPolicy.PolicyType.PROMOTION) {
            return "Discounted warranty plan valid for " + policy.getDurationPeriod() + " months on selected parts.";
        } else {
            return "Covers standard components such as basic battery pack, single motor, and charger.";
        }
    }

    String getConditionDescription(WarrantyPolicy policy) {
        if (policy.getType() == WarrantyPolicy.PolicyType.PROMOTION) {
            return "Warranty void if misuse, modification, or poor maintenance occurs.";
        } else {
            return "Warranty applies only to manufacturer defects; not valid for damage due to impact or overheating.";
        }
    }

    public List<GetPartClaimResponse> handleGetPartClaim(Long claimId) {
        List<PartClaim> partClaims = partClaimRepository.findByWarrantyClaimId(claimId);
        List<GetPartClaimResponse> responses = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (PartClaim partClaim : partClaims) {
            Part part = partClaim.getPart();

            // Lọc policy còn hiệu lực và đang ACTIVE
            List<PartPolicy> validPolicies = part.getPartPolicies().stream()
                    .filter(p -> p.getStatus() == PartPolicy.Status.ACTIVE)
                    .filter(p -> (p.getStartDate() == null || !p.getStartDate().isAfter(today)))
                    .filter(p -> (p.getEndDate() == null || !p.getEndDate().isBefore(today)))
                    .collect(Collectors.toList());

            PartPolicy partPolicy = validPolicies.stream()
                    .sorted((a, b) -> b.getEndDate().compareTo(a.getEndDate()))
                    .findFirst()
                    .orElse(null);

            WarrantyPolicy policy = (partPolicy != null) ? partPolicy.getWarrantyPolicy() : null;

            // 🔹 Lấy giá hiện tại từ PartPriceHistory
            Double currentPrice = part.getPartPriceHistories().stream()
                    .filter(h -> (h.getStartDate() == null || !h.getStartDate().isAfter(today)))
                    .filter(h -> (h.getEndDate() == null || !h.getEndDate().isBefore(today)))
                    .sorted((a, b) -> b.getStartDate().compareTo(a.getStartDate()))
                    .map(PartPriceHistory::getPrice)
                    .findFirst()
                    .orElse(0.0);

            String coverage = (policy != null)
                    ? getCoverageDescription(policy)
                    : "No warranty coverage information available.";

            String conditions = (policy != null)
                    ? getConditionDescription(policy)
                    : "No warranty conditions specified.";

            GetPartClaimResponse response = GetPartClaimResponse.builder()
                    .partClaimId(partClaim.getId())
                    .partClaimName(part.getName())
                    .status(partClaim.getStatus().name())
                    .estimatedCost(currentPrice)
                    .policyName(policy != null ? policy.getName() : "N/A")
                    .description(part.getDescription())
                    .durationPeriod(policy != null ? policy.getDurationPeriod() : 0)
                    .effect(partPolicy != null ? partPolicy.getStartDate() : null)
                    .coverage(coverage)
                    .conditional(conditions)
                    .build();

            responses.add(response);
        }

        return responses;
    }

    public String handleChangeStatusPartClaim(ChangeStatusPartClaimRequest request, long claimId, long partClaimId,
            long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        if (user.getRole() != User.Role.EVM_STAFF) {
            return "❌ Access denied: only EVM_STAFF can change part claim status.";
        }

        try {
            if (request == null || request.getStatus() == null) {
                return "Invalid request: status is missing.";
            }

            // 1️⃣ Find PartClaim by ID
            PartClaim partClaim = partClaimRepository.findById(partClaimId)
                    .orElseThrow(() -> new NoSuchElementException("PartClaim not found with ID: " + partClaimId));

            // 2️⃣ Update status
            PartClaim.ClaimStatus newStatus = PartClaim.ClaimStatus.valueOf(request.getStatus().toUpperCase());
            partClaim.setStatus(newStatus);
            partClaimRepository.save(partClaim);

            eventPublisher.publishEvent(new EntityUpdatedEvent<>(this, partClaim));

            if (newStatus == PartClaim.ClaimStatus.REJECTED) {
                WarrantyClaim claim = partClaim.getWarrantyClaim();
                RepairOrder ro = claim.getRepairOrder();
                if (ro != null) {
                    Long partId = partClaim.getPart().getId();
                    String partName = partClaim.getPart().getName();

                    // 🔹 Update RepairDetail
                    List<RepairDetail> details = repairDetailRepository
                            .findByRepairOrderIdAndPartId(ro.getId(), partId);
                    for (RepairDetail rd : details) {
                        rd.setStatus(RepairDetail.DetailStatus.REJECTED);
                    }
                    repairDetailRepository.saveAll(details);

                    // 🔹 Update RepairStep liên quan đến part
                    List<RepairStep> relatedSteps = ro.getSteps().stream()
                            .filter(step -> step.getTitle().toLowerCase().contains(partName.toLowerCase()))
                            .collect(Collectors.toList());

                    for (RepairStep step : relatedSteps) {
                        step.setStatus(RepairStep.StepStatus.REJECTED);
                    }
                    repairStepRepository.saveAll(relatedSteps);
                }

                // 🔹 Kiểm tra nếu toàn bộ PartClaim của claim đều bị từ chối
                Long warrantyClaimId = claim.getId();
                List<PartClaim> allPartClaims = partClaimRepository.findByWarrantyClaimId(warrantyClaimId);

                boolean allRejected = allPartClaims.stream()
                        .allMatch(pc -> pc.getStatus() == PartClaim.ClaimStatus.REJECTED);

                if (allRejected) {
                    // 🔸 Cập nhật claim
                    claim.setStatus(WarrantyClaim.ClaimStatus.REJECTED);
                    warrantyClaimRepository.save(claim);
                    eventPublisher.publishEvent(new EntityUpdatedEvent<>(this, claim));

                    // 🔸 Cập nhật luôn repair order
                    if (ro != null) {
                        ro.setStatus(RepairOrder.OrderStatus.CANCELLED);
                        repairOrderRepository.save(ro);
                        eventPublisher.publishEvent(new EntityUpdatedEvent<>(this, ro));

                        // Optionally: reject tất cả detail/step còn lại
                        ro.getRepairDetails().forEach(rd -> rd.setStatus(RepairDetail.DetailStatus.REJECTED));
                        repairDetailRepository.saveAll(ro.getRepairDetails());
                        eventPublisher.publishEvent(new EntityUpdatedEvent<>(this, ro.getRepairDetails()));

                        ro.getSteps().forEach(step -> step.setStatus(RepairStep.StepStatus.CANCELLED));
                        repairStepRepository.saveAll(ro.getSteps());
                        eventPublisher.publishEvent(new EntityUpdatedEvent<>(this, ro.getSteps()));

                    }
                }
            }

            // 3️⃣ Get current part
            Part part = partClaim.getPart();
            LocalDate today = LocalDate.now();

            // 4️⃣ Find active & valid policy
            PartPolicy activePolicy = part.getPartPolicies().stream()
                    .filter(p -> p.getStatus() == PartPolicy.Status.ACTIVE)
                    .filter(p -> (p.getStartDate() == null || !p.getStartDate().isAfter(today)))
                    .filter(p -> (p.getEndDate() == null || !p.getEndDate().isBefore(today)))
                    .sorted((a, b) -> b.getEndDate().compareTo(a.getEndDate()))
                    .findFirst()
                    .orElse(null);

            WarrantyPolicy warranty = (activePolicy != null) ? activePolicy.getWarrantyPolicy() : null;

            // 5️⃣ Return message
            return String.format(
                    "✅ Part claim status updated successfully!\n" +
                            "Part: %s\n" +
                            "New Status: %s\n" +
                            "Warranty Policy: %s\n" +
                            "Claim ID: %d\n" +
                            "PartClaim ID: %d",
                    part.getName(),
                    partClaim.getStatus().name(),
                    warranty != null ? warranty.getName() : "No active warranty policy found",
                    claimId,
                    partClaimId);

        } catch (Exception e) {
            return "❌ Failed to update part claim status: " + e.getMessage();
        }
    }

    @Override
    @Transactional
    public String handleUpdatePartQuantities(long claimId, List<PartClaimRequest> updates, long userId) {
        // 1. Lấy claim
        WarrantyClaim claim = warrantyClaimRepository.findById(claimId)
                .orElseThrow(() -> new NoSuchElementException("Warranty claim not found: " + claimId));

        // 2. Lấy tất cả PartClaim thuộc claim này
        List<PartClaim> partClaims = partClaimRepository.findByWarrantyClaimId(claimId);

        // 3. Tạo map để dễ update
        Map<Long, PartClaimRequest> updateMap = updates.stream()
                .collect(Collectors.toMap(PartClaimRequest::getId, Function.identity()));

        for (PartClaim pc : partClaims) {
            PartClaimRequest update = updateMap.get(pc.getPart().getId());
            if (update != null) {
                // Cập nhật quantity, đảm bảo > 0 theo constraint nếu cần
                long newQty = Math.max(update.getQuantity(), 1);
                pc.setQuantity(newQty);
            }
        }

        // 4. Lưu tất cả
        partClaimRepository.saveAll(partClaims);

        return "Part quantities updated successfully";
    }

}