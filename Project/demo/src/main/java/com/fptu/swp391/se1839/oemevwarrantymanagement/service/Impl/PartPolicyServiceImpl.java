package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CreatePartPolicyRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllPartPolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartPolicyCodeResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartPolicyDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartPolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartWarrantyInfoResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Part;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartPolicy;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.VehiclePart;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.WarrantyPolicy;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartPolicyRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PolicyRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehiclePartRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PartPolicyService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartPolicyServiceImpl implements PartPolicyService {
        PartPolicyRepository partPolicyRepository;
        PartRepository partRepository;
        PolicyRepository policyRepository;
        VehiclePartRepository vehiclePartRepository;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public GetAllPartPolicyResponse handleGetAllPartPolicies() {
                List<PartPolicy> partPolicies = partPolicyRepository.findAll();

                List<PartPolicyResponse> policyInfos = partPolicies.stream()
                                .map(p -> PartPolicyResponse.builder()
                                                .id(p.getId())
                                                .partName(p.getPart() != null ? p.getPart().getName() : null)
                                                .partCode(p.getPart() != null ? p.getPart().getCode() : null)
                                                .policyCode(p.getWarrantyPolicy() != null
                                                                ? p.getWarrantyPolicy().getCode()
                                                                : null)
                                                .startDate(p.getStartDate() != null ? p.getStartDate().format(formatter)
                                                                : null)
                                                .endDate(p.getEndDate() != null ? p.getEndDate().format(formatter)
                                                                : null)
                                                .status(PartPolicyResponse.Status.valueOf(p.getStatus().name()))
                                                .build())
                                .collect(Collectors.toList());

                return GetAllPartPolicyResponse.builder()
                                .partPolicies(policyInfos)
                                .build();
        }

        @Override
        public PartPolicyDetailResponse handleGetPartPolicyById(Long partPolicyId) {
                PartPolicy partPolicy = partPolicyRepository.findById(partPolicyId)
                                .orElseThrow(() -> new IllegalArgumentException("Part Policy is not found"));

                Part part = partPolicy.getPart();
                WarrantyPolicy warranty = partPolicy.getWarrantyPolicy();

                return PartPolicyDetailResponse.builder()
                                .partName(part != null ? part.getName() : null)
                                .partCategory(part != null && part.getPartCategory() != null ? part.getPartCategory()
                                                : null)
                                .durationPeriod(warranty != null ? warranty.getDurationPeriod() : null)
                                .mileageLimit(warranty != null ? warranty.getMileageLimit() : null)
                                .startDate(partPolicy.getStartDate() != null
                                                ? partPolicy.getStartDate().format(formatter)
                                                : null)
                                .endDate(partPolicy.getEndDate() != null ? partPolicy.getEndDate().format(formatter)
                                                : null)
                                .description(warranty != null ? warranty.getDescription() : null)
                                .build();
        }

        @Override
        public PartPolicyResponse handleCreatePartPolicy(CreatePartPolicyRequest request) {
                try {
                        // ===== Validate input =====
                        if (request.getPartCode() == null || request.getPolicyCode() == null)
                                throw new IllegalArgumentException("Part code and Policy code are required");
                        if (request.getStartDate() == null)
                                throw new IllegalArgumentException("Start date is required");
                        if (request.getEndDate() == null)
                                throw new IllegalArgumentException("End date is required");

                        // ===== Lấy Part và Policy theo code =====
                        Part part = partRepository.findByCode(request.getPartCode())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Part not found with code: " + request.getPartCode()));

                        WarrantyPolicy policy = policyRepository.findByCode(request.getPolicyCode())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "WarrantyPolicy not found with code: "
                                                                        + request.getPolicyCode()));

                        // ===== Chuyển đổi ngày =====
                        LocalDate startDate = LocalDate.parse(request.getStartDate(), formatter);
                        LocalDate endDate = LocalDate.parse(request.getEndDate(), formatter);

                        if (endDate.isBefore(startDate) || endDate.isEqual(startDate))
                                throw new IllegalArgumentException("End date must be after start date");

                        // ===== Kiểm tra trùng thời gian =====
                        boolean exists = partPolicyRepository.existsByPartIdAndWarrantyPolicyIdAndDateRangeOverlap(
                                        part.getId(), policy.getId(), startDate, endDate);

                        if (exists && policy.getType() != WarrantyPolicy.PolicyType.PROMOTION)
                                throw new IllegalArgumentException(
                                                "A policy for this part already exists during this period");

                        // ===== Tạo và lưu chính sách mới =====
                        PartPolicy newPolicy = PartPolicy.builder()
                                        .part(part)
                                        .warrantyPolicy(policy)
                                        .startDate(startDate)
                                        .endDate(endDate)
                                        .status(PartPolicy.Status.ACTIVE)
                                        .build();

                        PartPolicy saved = partPolicyRepository.save(newPolicy);

                        // ===== Tạo response trả về =====
                        return PartPolicyResponse.builder()
                                        .id(saved.getId())
                                        .partName(saved.getPart().getName())
                                        .partCode(saved.getPart().getCode())
                                        .policyCode(saved.getWarrantyPolicy().getCode())
                                        .startDate(formatter.format(saved.getStartDate()))
                                        .endDate(formatter.format(saved.getEndDate()))
                                        .build();

                } catch (DateTimeParseException e) {
                        throw new RuntimeException("Invalid date format, expected yyyy-MM-dd");
                }
        }

        @Override
        public PartPolicyCodeResponse handleGetPartPolicyCode() {
                List<Part> parts = partRepository.findAll();
                List<WarrantyPolicy> policies = policyRepository.findAll();

                Map<String, String> partMap = parts.stream()
                                .collect(Collectors.toMap(Part::getCode, Part::getName));

                Map<String, String> policyMap = policies.stream()
                                .collect(Collectors.toMap(WarrantyPolicy::getCode, WarrantyPolicy::getName));

                return PartPolicyCodeResponse.builder()
                                .partMap(partMap)
                                .policyMap(policyMap)
                                .build();
        }

        @Override
        public PartPolicyResponse handleUpdateStatusPartPolicy(Long partPolicyId) {
                PartPolicy partPolicy = partPolicyRepository.findById(partPolicyId)
                                .orElseThrow(() -> new NoSuchElementException(
                                                "PartPolicy with ID " + partPolicyId + " not found"));
                if (partPolicy.getStatus() == PartPolicy.Status.INACTIVE) {
                        List<PartPolicy> activePolicies = partPolicyRepository
                                        .findByPartId(partPolicy.getPart().getId()).stream()
                                        .filter(pp -> !pp.getId().equals(partPolicy.getId())) // bỏ qua chính nó
                                        .filter(pp -> pp.getStatus() == PartPolicy.Status.ACTIVE)
                                        .filter(pp -> isOverlapping(
                                                        partPolicy.getStartDate(),
                                                        partPolicy.getEndDate(),
                                                        pp.getStartDate(),
                                                        pp.getEndDate()))
                                        .toList();

                        if (!activePolicies.isEmpty()) {
                                throw new IllegalStateException(
                                                "This part already has an active policy during the selected period.");
                        }
                } else {
                        partPolicy.setStatus(PartPolicy.Status.INACTIVE);
                }
                PartPolicy saved = partPolicyRepository.save(partPolicy);
                return PartPolicyResponse.builder()
                                .id(saved.getId())
                                .partName(saved.getPart().getName())
                                .partCode(saved.getPart().getCode())
                                .policyCode(saved.getWarrantyPolicy().getCode())
                                .startDate(saved.getStartDate() != null ? formatter.format(saved.getStartDate()) : null)
                                .endDate(saved.getEndDate() != null ? formatter.format(saved.getEndDate()) : null)
                                .status(PartPolicyResponse.Status.valueOf(saved.getStatus().name()))
                                .build();
        }

        private boolean isOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
                LocalDate actualEnd1 = end1 != null ? end1 : LocalDate.MAX;
                LocalDate actualEnd2 = end2 != null ? end2 : LocalDate.MAX;
                return !actualEnd1.isBefore(start2) && !actualEnd2.isBefore(start1);
        }

        public PartWarrantyInfoResponse getWarrantyInfoBySerial(String serialNumber) {
                VehiclePart vp = vehiclePartRepository.findBySerial(serialNumber)
                                .orElseThrow(() -> new IllegalArgumentException("Serial number not found"));

                var vehicle = vp.getVehicle();
                var part = vp.getPart();

                PartPolicy latestPolicy = partPolicyRepository
                                .findLatestValidPolicy(part.getId(), LocalDate.now())
                                .orElseThrow(() -> new IllegalStateException("No active policy found for this part"));

                var warranty = latestPolicy.getWarrantyPolicy();

                LocalDate start = vehicle.getProductionDate();
                LocalDate end = start.plusMonths(warranty.getDurationPeriod());

                return PartWarrantyInfoResponse.builder()
                                .policyName(warranty.getName())
                                .startDate(start.format(formatter))
                                .endDate(end.format(formatter))
                                .mileageLimit(warranty.getMileageLimit())
                                .build();
        }
}