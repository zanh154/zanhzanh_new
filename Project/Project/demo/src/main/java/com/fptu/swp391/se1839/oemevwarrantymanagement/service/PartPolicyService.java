package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CreatePartPolicyRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllPartPolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartPolicyCodeResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartPolicyDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartPolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartWarrantyInfoResponse;

public interface PartPolicyService {
    GetAllPartPolicyResponse handleGetAllPartPolicies();

    PartPolicyResponse handleCreatePartPolicy(CreatePartPolicyRequest request);

    PartPolicyDetailResponse handleGetPartPolicyById(Long partPolicyId);

    PartPolicyCodeResponse handleGetPartPolicyCode();

    PartPolicyResponse handleUpdateStatusPartPolicy(Long partPolicyId);

    PartWarrantyInfoResponse getWarrantyInfoBySerial(String serialNumber);

}
