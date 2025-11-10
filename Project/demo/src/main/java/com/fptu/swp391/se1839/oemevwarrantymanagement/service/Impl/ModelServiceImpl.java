package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.DetailModelResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ModelResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ModelPolicyDetailResponse;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Model;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartPolicy;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ModelRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartPolicyRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.ModelService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

        private final ModelRepository modelRepository;
        private final PartPolicyRepository partPolicyRepository;

        @Override
        public List<ModelResponse> getAllModels() {
                List<Model> models = modelRepository.findAll();
                return models.stream()
                                .map(model -> ModelResponse.builder()
                                                .id(model.getId())
                                                .name(model.getName())
                                                .releaseYear(model.getReleaseYear())
                                                .description(model.getDescription())
                                                .isInProduction(model.getIsInProduction())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        public DetailModelResponse getModelDetail(Long modelId) {
                Model model = modelRepository.findById(modelId)
                                .orElseThrow(() -> new NoSuchElementException(
                                                "Model with id " + modelId + " not found"));

                List<ModelPolicyDetailResponse> partPolicyDetails = model.getModelParts().stream()
                                .flatMap(mp -> partPolicyRepository.findByPartId(mp.getPart().getId()).stream()
                                                .filter(pp -> pp.getStatus() == PartPolicy.Status.ACTIVE
                                                                && (pp.getEndDate() == null || !pp.getEndDate()
                                                                                .isBefore(LocalDate.now())))
                                                .map(pp -> ModelPolicyDetailResponse.builder()
                                                                .partId(mp.getPart().getId())
                                                                .partName(mp.getPart().getName())
                                                                .policyId(pp.getWarrantyPolicy().getId())
                                                                .policyName(pp.getWarrantyPolicy().getName())
                                                                .description(pp.getWarrantyPolicy().getDescription())
                                                                .durationPeriod(pp.getWarrantyPolicy()
                                                                                .getDurationPeriod())
                                                                .mileageLimit(pp.getWarrantyPolicy().getMileageLimit())
                                                                .startDate(pp.getStartDate() != null
                                                                                ? pp.getStartDate().toString()
                                                                                : null)
                                                                .endDate(pp.getEndDate() != null
                                                                                ? pp.getEndDate().toString()
                                                                                : null)
                                                                .build()))
                                .collect(Collectors.toList());

                return DetailModelResponse.builder()
                                .partPolicies(partPolicyDetails)
                                .build();
        }
}