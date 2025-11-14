package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.util.List;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.VehicleInspectionRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.VehicleInspectionResponse;

public interface VehicleInspectionService {
    // Tạo báo cáo kiểm tra
    VehicleInspectionResponse createInspection(VehicleInspectionRequest request, Long inspectorId);

    // Cập nhật báo cáo kiểm tra
    VehicleInspectionResponse updateInspection(Long inspectionId, VehicleInspectionRequest request);

    // Lấy danh sách theo service center
    List<VehicleInspectionResponse> getInspectionsByServiceCenter(Long serviceCenterId);

    // Lấy danh sách theo người kiểm tra
    List<VehicleInspectionResponse> getInspectionsByInspector(Long inspectorId);

    // Lấy báo cáo kiểm tra theo warranty claim
    VehicleInspectionResponse getInspectionByClaimId(Long claimId);

    // Lấy chi tiết báo cáo kiểm tra
    VehicleInspectionResponse getInspectionById(Long inspectionId);

    // Xóa báo cáo kiểm tra
    void deleteInspection(Long inspectionId);
}