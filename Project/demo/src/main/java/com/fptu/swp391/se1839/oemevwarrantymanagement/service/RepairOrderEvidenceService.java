package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.io.IOException;
import java.util.List;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.AllEvidenceRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.AllEvidenceResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairOrderEvidence;

public interface RepairOrderEvidenceService {
    List<RepairOrderEvidence> handleCreateEvidence(long repairOrderId, AllEvidenceRequest request, long userId);

    List<AllEvidenceResponse> getEvidenceByRepairOrderId(Long repairOrderId) throws IOException;
}
