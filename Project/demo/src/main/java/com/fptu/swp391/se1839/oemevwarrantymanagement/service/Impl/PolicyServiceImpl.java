package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CreatePolicyRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.UpdatePolicyRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.CreatePolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.DeletePolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllPolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.UpdatePolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartPolicy;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.WarrantyPolicy;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartPolicyRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PolicyRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PolicyService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PolicyServiceImpl implements PolicyService {
        final PolicyRepository policyRepository;
        final PartPolicyRepository partPolicyRepository;

        @Override
        public GetAllPolicyResponse handleGetAllPolicy() {
                List<WarrantyPolicy> policies = policyRepository.findAll();

                List<PolicyResponse> policiesResponse = policies.stream()
                                .map(policy -> PolicyResponse.builder()
                                                .id(policy.getId())
                                                .name(policy.getName())
                                                .description(policy.getDescription())
                                                .durationPeriod(policy.getDurationPeriod())
                                                .mileageLimit(policy.getMileageLimit())
                                                .code(policy.getCode())
                                                .policyType(PolicyResponse.PolicyType.valueOf(policy.getType().name()))
                                                .status(PolicyResponse.Status.valueOf(policy.getStatus().name()))
                                                .build())
                                .toList();

                return GetAllPolicyResponse.builder()
                                .policyList(policiesResponse)
                                .build();
        }

        @Override
        public CreatePolicyResponse handleCreatePolicy(CreatePolicyRequest request) {
                if (policyRepository.existsByName(request.getName())) {
                        return CreatePolicyResponse.builder()
                                        .success(false)
                                        .message("Warranty policy name already exists")
                                        .policy(null)
                                        .build();
                }
                if (policyRepository.existsByCode(request.getCode())) {
                        return CreatePolicyResponse.builder()
                                        .success(false)
                                        .message("Warranty policy code already exists")
                                        .policy(null)
                                        .build();
                }
                WarrantyPolicy policy = WarrantyPolicy.builder()
                                .code(request.getCode())
                                .name(request.getName())
                                .description(request.getDescription())
                                .durationPeriod(request.getDurationPeriod())
                                .mileageLimit(request.getMileageLimit())
                                .type(request.getType() == null ? WarrantyPolicy.PolicyType.NORMAL
                                                : WarrantyPolicy.PolicyType.valueOf(request.getType().name()))
                                .build();

                WarrantyPolicy saved = policyRepository.save(policy);
                log.info("Created new WarrantyPolicy with id: {}", saved.getId());

                return CreatePolicyResponse.builder()
                                .success(true)
                                .message("Warranty policy created successfully")
                                .policy(saved)
                                .build();
        }

        @Override
        public PolicyResponse handleGetPolicyById(Long policyId) {
                WarrantyPolicy policy = policyRepository.findById(policyId)
                                .orElseThrow(() -> new NoSuchElementException("Policy is not existed"));
                return PolicyResponse.builder()
                                .id(policy.getId())
                                .name(policy.getName())
                                .description(policy.getDescription())
                                .durationPeriod(policy.getDurationPeriod())
                                .mileageLimit(policy.getMileageLimit())
                                .build();
        }

        public UpdatePolicyResponse handleUpdatePolicy(Long policyId, UpdatePolicyRequest request) {
                WarrantyPolicy existingPolicy = policyRepository.findById(policyId)
                                .orElseThrow(() -> new NoSuchElementException(
                                                "Policy with ID " + policyId + " not found"));

                WarrantyPolicy otherPolicy = policyRepository.findByName(request.getName());
                if (otherPolicy != null && !otherPolicy.getId().equals(policyId)) {
                        throw new IllegalArgumentException("Warranty policy name already exists");
                }

                existingPolicy.setName(request.getName());
                existingPolicy.setDescription(request.getDescription());
                existingPolicy.setDurationPeriod(request.getDurationPeriod());
                existingPolicy.setMileageLimit(request.getMileageLimit());
                existingPolicy.setType(WarrantyPolicy.PolicyType.valueOf(request.getPolicyType().name()));

                WarrantyPolicy updated = policyRepository.save(existingPolicy);
                log.info("Updated WarrantyPolicy with id: {}", updated.getId());

                return UpdatePolicyResponse.builder()
                                .policy(updated)
                                .build();
        }

        @Override
        public DeletePolicyResponse handleDeletePolicy(Long policyId) {
                WarrantyPolicy policy = policyRepository.findById(policyId)
                                .orElseThrow(() -> new NoSuchElementException("Policy not found with ID " + policyId));

                LocalDate today = LocalDate.now();
                List<PartPolicy> unexpiredParts = partPolicyRepository.findUnexpiredPartPolicies(policyId, today);
                if (!unexpiredParts.isEmpty()) {
                        throw new IllegalArgumentException(
                                        "Cannot delete policy: there are still active part policies.");
                }

                policyRepository.delete(policy);
                log.info("Deleted WarrantyPolicy with id: {}", policyId);
                return DeletePolicyResponse.builder()
                                .success(true)
                                .message("Warranty policy deleted successfully.")
                                .build();
        }

        @Override
        @Transactional
        public String updatePolicyStatus(Long policyId) {

                // Lấy policy theo ID
                WarrantyPolicy policy = policyRepository.findById(policyId)
                                .orElseThrow(() -> new NoSuchElementException("Policy not found with id: " + policyId));

                // Nếu policy đang ACTIVE → chuyển sang INACTIVE
                if (policy.getStatus() == WarrantyPolicy.Status.ACTIVE) {
                        LocalDate today = LocalDate.now();

                        // Lấy tất cả PartPolicy còn hạn (tức là chưa hết hạn và đang ACTIVE)
                        List<PartPolicy> validPartPolicies = partPolicyRepository
                                        .findUnexpiredPartPolicies(policyId, today)
                                        .stream()
                                        .filter(p -> p.getStatus() == PartPolicy.Status.ACTIVE)
                                        .toList();

                        if (!validPartPolicies.isEmpty()) {
                                // Cập nhật status và endDate cho từng partpolicy
                                for (PartPolicy partPolicy : validPartPolicies) {
                                        partPolicy.setStatus(PartPolicy.Status.INACTIVE);
                                        partPolicy.setEndDate(today);
                                }

                                partPolicyRepository.saveAll(validPartPolicies);
                                log.info("Inactivated {} active PartPolicies for policy {}.", validPartPolicies.size(),
                                                policy.getCode());
                        } else {
                                log.info("No active PartPolicies found for policy {} to deactivate.", policy.getCode());
                        }

                        // Cập nhật trạng thái policy
                        policy.setStatus(WarrantyPolicy.Status.INACTIVE);
                        policyRepository.save(policy);

                        return String.format(
                                        "Policy %s set to INACTIVE. %d PartPolicies were deactivated.",
                                        policy.getCode(), validPartPolicies.size());
                }

                // Nếu policy đang INACTIVE → chuyển sang ACTIVE
                else if (policy.getStatus() == WarrantyPolicy.Status.INACTIVE) {
                        policy.setStatus(WarrantyPolicy.Status.ACTIVE);
                        policyRepository.save(policy);
                        log.info("Activated WarrantyPolicy {}", policy.getCode());
                        return String.format("Policy %s has been reactivated.", policy.getCode());
                }

                // Trường hợp không hợp lệ (phòng ngừa enum sai)
                throw new IllegalStateException("Unsupported policy status: " + policy.getStatus());
        }
}