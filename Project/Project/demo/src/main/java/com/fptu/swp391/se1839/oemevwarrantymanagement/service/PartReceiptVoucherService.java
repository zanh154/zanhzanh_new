package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.util.List;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.PartReceiptVoucherRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartReceiptVoucherResponse;

public interface PartReceiptVoucherService {
    // Tạo phiếu nhận phụ tùng mới
    PartReceiptVoucherResponse createVoucher(PartReceiptVoucherRequest request, Long receiverId);

    // Cập nhật trạng thái phiếu
    PartReceiptVoucherResponse updateStatus(Long voucherId, String status);

    // Lấy danh sách phiếu theo service center
    List<PartReceiptVoucherResponse> getVouchersByServiceCenter(Long serviceCenterId);

    // Lấy danh sách phiếu theo người nhận
    List<PartReceiptVoucherResponse> getVouchersByReceiver(Long receiverId);

    // Lấy chi tiết phiếu
    PartReceiptVoucherResponse getVoucherById(Long voucherId);

    // Xóa phiếu
    void deleteVoucher(Long voucherId);
}