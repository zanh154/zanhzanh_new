package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.util.List;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.AttachSerialRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ChangeStatusRepairStepResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetRepairStepResponse;

public interface RepairStepService {

    List<GetRepairStepResponse> handleGetRepairStep(long epairOrderId);

    ChangeStatusRepairStepResponse completeRepairStep(long repairStepId);

    void attachNewSerialNumbers(long repairOrderId, AttachSerialRequest request);
}
