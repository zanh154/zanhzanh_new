package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllPartResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartCategoryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartListResponse;

public interface PartService {
    PartCategoryResponse handleListCategory(String vin);

    PartListResponse handlePartList(String category, String vin);

    public GetAllPartResponse handleGetPartList();
}
