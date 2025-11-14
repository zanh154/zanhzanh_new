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
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartInventoryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PartInventoryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class PartInventoryController {

    final PartInventoryService partInventoryService;

    @GetMapping("/part-inventories")
    @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
    public ResponseEntity<ApiResponse<List<PartInventoryResponse>>> getAllPartInventories() {
        List<PartInventoryResponse> inventories = partInventoryService.getAllPartInventories();
        var result = ApiResponse.<List<PartInventoryResponse>>builder()
                .status(HttpStatus.OK.toString())
                .message("Get all part inventories successfully")
                .data(inventories)
                .build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/part-inventory/service-center/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
    public ResponseEntity<ApiResponse<List<PartInventoryResponse>>> getPartInventoriesByServiceCenter(
            @PathVariable("id") Long id) {
        List<PartInventoryResponse> inventories = partInventoryService.getPartInventoriesByServiceCenter(id);
        var result = ApiResponse.<List<PartInventoryResponse>>builder()
                .status(HttpStatus.OK.toString())
                .message("Get part inventories by service center successfully")
                .data(inventories)
                .build();
        return ResponseEntity.ok(result);
    }
}