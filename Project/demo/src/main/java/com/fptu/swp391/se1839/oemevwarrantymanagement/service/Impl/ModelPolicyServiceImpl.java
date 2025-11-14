package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ModelPolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ModelPolicy;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Vehicle;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ModelPolicyRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehicleRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.ModelPolicyService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ModelPolicyServiceImpl implements ModelPolicyService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ModelPolicyRepository modelPolicyRepository;

    @Override
    public ModelPolicyResponse getWarrantyPolicyByVin(String vin) {
        // Lấy thông tin xe
        Vehicle vehicle = vehicleRepository.findById(vin)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with VIN: " + vin));

        Long modelId = vehicle.getModel().getId();
        LocalDate purchaseDate = vehicle.getPurchaseDate();

        // Tìm chính sách áp dụng cho model tại thời điểm mua xe
        ModelPolicy modelPolicy = modelPolicyRepository
                .findActivePolicyByModelAndDate(modelId, purchaseDate)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No valid warranty policy found for this model and purchase date"));

        // Tính ngày hết hạn bảo hành
        LocalDate warrantyEndDate = purchaseDate.plusMonths(modelPolicy.getWarrantyPolicy().getDurationPeriod());

        // Trả dữ liệu
        return ModelPolicyResponse.builder()
                .modelName(vehicle.getModel().getName())
                .policyName(modelPolicy.getWarrantyPolicy().getName())
                .durationMonths(modelPolicy.getWarrantyPolicy().getDurationPeriod())
                .mileageLimit(modelPolicy.getWarrantyPolicy().getMileageLimit())
                .purchaseDate(purchaseDate)
                .warrantyEndDate(warrantyEndDate)
                .build();
    }
}