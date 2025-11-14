package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.util.List;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartInventoryResponse;

public interface PartInventoryService {
    List<PartInventoryResponse> getAllPartInventories();

    List<PartInventoryResponse> getPartInventoriesByServiceCenter(Long serviceCenterId);

    int handleCalculatePartAvailability(long serviceCenterId);

    int countLowStockParts(long serviceCenterId);
}
