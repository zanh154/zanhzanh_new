package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.util.List;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.ChangeStatusPartClaimRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.PartClaimRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ClaimsByComponentResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.claimsByCategoryResponse;

public interface PartClaimService {
    List<claimsByCategoryResponse> calculateClaimsByCategory(Long serviceCenterId);

    List<ClaimsByComponentResponse> calculateClaimsByComponent(Long serviceCenterId);

    String handleChangeStatusPartClaim(ChangeStatusPartClaimRequest request, long partClaimId,
            long userId);

    String handleUpdatePartQuantities(long claimId, List<PartClaimRequest> updates, long userId);

    String handleSubmitPartClaims(long claimId, long userId, List<PartClaimRequest> requests);
}
