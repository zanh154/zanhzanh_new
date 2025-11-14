package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.ApproveOrRejectPartRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CreatePartSupplyRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.CreatePartSupplyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllPartSupplyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartSupplyDetailResponse;

public interface PartSupplyService {
    CreatePartSupplyResponse handleCreatePartSupply(CreatePartSupplyRequest request, Long userId, Long serviceCenterId);

    GetAllPartSupplyResponse handleGetAllPartSupplies();

    PartSupplyDetailResponse handleGetPartSupplyDetail(Long id);

    PartSupplyDetailResponse handleReviewPartSupply(ApproveOrRejectPartRequest request, Long staffId);
}