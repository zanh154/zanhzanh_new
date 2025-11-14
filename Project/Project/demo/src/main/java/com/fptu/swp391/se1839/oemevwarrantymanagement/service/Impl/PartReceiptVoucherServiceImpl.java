package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.PartReceiptDetailRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.PartReceiptVoucherRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartReceiptDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartReceiptVoucherResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartReceiptDetail;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartReceiptVoucher;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ServiceCenter;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.VehiclePart;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartReceiptVoucherRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ServiceCenterRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.UserRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehiclePartRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PartReceiptVoucherService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PartReceiptVoucherServiceImpl implements PartReceiptVoucherService {

    private final PartReceiptVoucherRepository voucherRepository;
    private final ServiceCenterRepository serviceCenterRepository;
    private final UserRepository userRepository;
    private final VehiclePartRepository partRepository;

    @Override
    @Transactional
    public PartReceiptVoucherResponse createVoucher(PartReceiptVoucherRequest request, Long receiverId) {
        // Validate service center
        if (request.getServiceCenterId() == null) {
            throw new IllegalArgumentException("Service center ID cannot be null");
        }
        ServiceCenter serviceCenter = serviceCenterRepository.findById(request.getServiceCenterId())
                .orElseThrow(() -> new EntityNotFoundException("Service center not found"));

        // Validate receiver
        if (receiverId == null) {
            throw new IllegalArgumentException("Receiver ID cannot be null");
        }
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

        // Create voucher
        PartReceiptVoucher voucher = PartReceiptVoucher.builder()
                .serviceCenter(serviceCenter)
                .receiver(receiver)
                .receiveDate(LocalDateTime.now())
                .notes(request.getNotes())
                .status("PENDING")
                .build();

        // Add details
        for (PartReceiptDetailRequest detailRequest : request.getDetails()) {
            if (detailRequest.getPartId() == null) {
                throw new IllegalArgumentException("Part ID cannot be null");
            }
            VehiclePart part = partRepository.findById(detailRequest.getPartId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Part not found with ID: " + detailRequest.getPartId()));

            PartReceiptDetail detail = PartReceiptDetail.builder()
                    .receiptVoucher(voucher)
                    .part(part)
                    .quantity(detailRequest.getQuantity())
                    .condition(detailRequest.getCondition())
                    .notes(detailRequest.getNotes())
                    .build();

            voucher.getDetails().add(detail);
        }

        // Save
        voucher = voucherRepository.save(voucher);

        return mapToResponse(voucher);
    }

    @Override
    @Transactional
    public PartReceiptVoucherResponse updateStatus(Long voucherId, String status) {
        if (voucherId == null) {
            throw new IllegalArgumentException("Voucher ID cannot be null");
        }

        PartReceiptVoucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new EntityNotFoundException("Voucher not found"));

        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        voucher.setStatus(status);
        PartReceiptVoucher savedVoucher = voucherRepository.save(voucher);
        if (savedVoucher == null) {
            throw new RuntimeException("Failed to save voucher");
        }

        return mapToResponse(savedVoucher);
    }

    @Override
    public List<PartReceiptVoucherResponse> getVouchersByServiceCenter(Long serviceCenterId) {
        return voucherRepository.findByServiceCenterId(serviceCenterId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PartReceiptVoucherResponse> getVouchersByReceiver(Long receiverId) {
        return voucherRepository.findByReceiverId(receiverId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PartReceiptVoucherResponse getVoucherById(Long voucherId) {
        if (voucherId == null) {
            throw new IllegalArgumentException("Voucher ID cannot be null");
        }

        PartReceiptVoucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new EntityNotFoundException("Voucher not found"));

        return mapToResponse(voucher);
    }

    @Override
    @Transactional
    public void deleteVoucher(Long voucherId) {
        if (voucherId == null) {
            throw new IllegalArgumentException("Voucher ID cannot be null");
        }

        if (!voucherRepository.existsById(voucherId)) {
            throw new EntityNotFoundException("Voucher not found");
        }

        voucherRepository.deleteById(voucherId);
    }

    private PartReceiptVoucherResponse mapToResponse(PartReceiptVoucher voucher) {
        List<PartReceiptDetailResponse> details = voucher.getDetails().stream()
                .map(detail -> PartReceiptDetailResponse.builder()
                        .id(detail.getId())
                        .partId(detail.getPart().getOldSerialNumber())
                        .partName(detail.getPart().getPart().getName()) // Get name from Part entity through VehiclePart
                        .quantity(detail.getQuantity())
                        .condition(detail.getCondition())
                        .notes(detail.getNotes())
                        .build())
                .collect(Collectors.toList());

        return PartReceiptVoucherResponse.builder()
                .id(voucher.getId())
                .serviceCenterId(voucher.getServiceCenter().getId())
                .serviceCenterName(voucher.getServiceCenter().getName())
                .receiverId(voucher.getReceiver().getId())
                .receiverName(voucher.getReceiver().getName())
                .receiveDate(voucher.getReceiveDate())
                .notes(voucher.getNotes())
                .status(voucher.getStatus())
                .details(details)
                .build();
    }
}