package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.util.List;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.VehicleHandoverRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.VehicleHandoverResponse;

public interface VehicleHandoverService {
    // Tạo biên bản bàn giao xe
    VehicleHandoverResponse createHandover(VehicleHandoverRequest request, Long staffId);

    // Cập nhật trạng thái
    VehicleHandoverResponse updateStatus(Long handoverId, String status, String rejectReason);

    // Lấy danh sách theo service center
    List<VehicleHandoverResponse> getHandoversByServiceCenter(Long serviceCenterId);

    // Lấy danh sách theo người bàn giao
    List<VehicleHandoverResponse> getHandoversByStaff(Long staffId);

    // Lấy chi tiết biên bản
    VehicleHandoverResponse getHandoverById(Long handoverId);

    // Xóa biên bản
    void deleteHandover(Long handoverId);
}