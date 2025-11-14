package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartReceiptVoucher;

public interface PartReceiptVoucherRepository extends JpaRepository<PartReceiptVoucher, Long> {
    // Tìm theo service center
    java.util.List<PartReceiptVoucher> findByServiceCenterId(Long serviceCenterId);

    // Tìm theo người nhận
    java.util.List<PartReceiptVoucher> findByReceiverId(Long receiverId);

    // Tìm theo trạng thái
    java.util.List<PartReceiptVoucher> findByStatus(String status);
}