package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.DetailModelResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ModelResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.ModelService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class ModelController {

    final ModelService modelService;

    @GetMapping("/models")
    @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
    public ResponseEntity<ApiResponse<List<ModelResponse>>> getAllModels() {
        List<ModelResponse> models = modelService.getAllModels();
        var result = ApiResponse.<List<ModelResponse>>builder()
                .status(HttpStatus.OK.toString())
                .message("Get all models successfully")
                .data(models)
                .build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/model/detail/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
    public ResponseEntity<ApiResponse<DetailModelResponse>> getModelDetail(@PathVariable("id") Long id) {
        DetailModelResponse models = modelService.getModelDetail(id);
        var result = ApiResponse.<DetailModelResponse>builder()
                .status(HttpStatus.OK.toString())
                .message("Get model detail successfully")
                .data(models)
                .build();
        return ResponseEntity.ok(result);
    }
}