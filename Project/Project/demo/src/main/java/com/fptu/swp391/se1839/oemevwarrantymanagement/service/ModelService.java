package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.util.List;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.DetailModelResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ModelResponse;

public interface ModelService {
    List<ModelResponse> getAllModels();

    DetailModelResponse getModelDetail(Long id);
}
