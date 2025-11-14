package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ModelPolicyResponse;

public interface ModelPolicyService {
    ModelPolicyResponse getWarrantyPolicyByVin(String vin);
}