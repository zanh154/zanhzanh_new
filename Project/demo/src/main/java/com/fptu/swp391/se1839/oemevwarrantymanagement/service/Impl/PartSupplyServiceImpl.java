package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.ApproveOrRejectPartRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CreatePartSupplyRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.PartApprovalDetailResquest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.CreatePartSupplyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllPartSupplyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartRequestDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartSupplyDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartSupplyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Part;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartInventory;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartRequestDetail;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartSupply;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ServiceCenter;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartInventoryRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartRequestDetailRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartSupplyRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ServiceCenterRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.UserRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PartSupplyService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartSupplyServiceImpl implements PartSupplyService {

        PartSupplyRepository partSupplyRepository;
        PartRequestDetailRepository partRequestDetailRepository;
        PartRepository partRepository;
        ServiceCenterRepository serviceCenterRepository;
        UserRepository userRepository;
        PartInventoryRepository partInventoryRepository;

        @Override
        public CreatePartSupplyResponse handleCreatePartSupply(CreatePartSupplyRequest request, Long userId,
                        Long serviceCenterId) {
                ServiceCenter serviceCenter = serviceCenterRepository.findById(serviceCenterId)
                                .orElseThrow(() -> new IllegalArgumentException("Service Center not found"));
                User creator = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                PartSupply partSupply = PartSupply.builder()
                                .serviceCenter(serviceCenter)
                                .createdBy(creator)
                                .createdDate(LocalDateTime.now())
                                .status(PartSupply.Status.PENDING)
                                .note(request.getNote())
                                .build();

                PartSupply savedSupply = partSupplyRepository.save(partSupply);
                request.getDetails().forEach(detailReq -> { // Corrected: Iterate over request.getDetails()
                        Part part = partRepository.findByCode(detailReq.getPartCode())
                                        .orElseThrow(() -> new IllegalArgumentException("Part not found"));
                        PartRequestDetail detail = PartRequestDetail.builder()
                                        .partRequest(savedSupply)
                                        .part(part)
                                        .requestedQuantity(detailReq.getRequestedQuantity())
                                        .build();
                        partRequestDetailRepository.save(detail);
                });

                log.info("Created PartSupply ID: {}", savedSupply.getId());
                return CreatePartSupplyResponse.builder()
                                .success(true)
                                .message("Part supply request created successfully")
                                .partSupplyId(savedSupply.getId())
                                .createdDate(savedSupply.getCreatedDate())
                                .requestedParts(request.getDetails().stream()
                                                .map(d -> "Part Code: " + d.getPartCode())
                                                .collect(Collectors.toList()))
                                .build();
        }

        @Override
        public GetAllPartSupplyResponse handleGetAllPartSupplies() {
                List<PartSupply> supplies = partSupplyRepository.findAllByOrderByCreatedDateDesc();

                List<PartSupplyResponse> responses = supplies.stream()
                                .map(supply -> PartSupplyResponse.builder()
                                                .id(supply.getId())
                                                .serviceCenterName(supply.getServiceCenter().getName())
                                                .createdBy(supply.getCreatedBy().getName())
                                                .createdDate(supply.getCreatedDate())
                                                .status(supply.getStatus().name())
                                                .note(supply.getNote())
                                                .build())
                                .collect(Collectors.toList());

                return GetAllPartSupplyResponse.builder()
                                .partSupplies(responses)
                                .build();
        }

        @Override
        public PartSupplyDetailResponse handleGetPartSupplyDetail(Long id) {
                PartSupply supply = partSupplyRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Part supply not found"));

                return PartSupplyDetailResponse.builder()
                                .id(supply.getId())
                                .serviceCenterName(supply.getServiceCenter().getName())
                                .createdBy(supply.getCreatedBy().getName())
                                .createdDate(supply.getCreatedDate())
                                .status(supply.getStatus().name())
                                .note(supply.getNote())
                                .details(supply.getDetails().stream()
                                                .map(d -> PartRequestDetailResponse.builder()
                                                                .id(d.getId())
                                                                .partCode(d.getPart().getCode())
                                                                .partName(d.getPart().getName())
                                                                .requestedQuantity(d.getRequestedQuantity())
                                                                .approvedQuantity(d.getApprovedQuantity())
                                                                .remark(d.getRemark())
                                                                .build())
                                                .toList())
                                .build();
        }

        @Override
        @Transactional
        public PartSupplyDetailResponse handleReviewPartSupply(ApproveOrRejectPartRequest request, Long staffId) {
                PartSupply supply = partSupplyRepository.findById(request.getPartSupplyId())
                                .orElseThrow(() -> new IllegalArgumentException("Part supply not found"));

                // Cập nhật approvedQuantity và remark
                if (request.getDetails() != null && !request.getDetails().isEmpty()) {
                        for (PartApprovalDetailResquest d : request.getDetails()) {
                                PartRequestDetail detail = partRequestDetailRepository.findById(d.getDetailId())
                                                .orElseThrow(() -> new IllegalArgumentException(
                                                                "Part request detail not found"));
                                detail.setApprovedQuantity(d.getApprovedQuantity());
                                detail.setRemark(d.getRemark());
                                partRequestDetailRepository.save(detail);
                        }
                }

                // Xử lý khi approve
                if ("APPROVE".equalsIgnoreCase(request.getAction())) {

                        for (PartApprovalDetailResquest d : request.getDetails()) {
                                PartRequestDetail detail = partRequestDetailRepository.findById(d.getDetailId())
                                                .orElseThrow(() -> new IllegalArgumentException(
                                                                "Part request detail not found"));
                                Part part = detail.getPart();
                                int approvedQty = d.getApprovedQuantity();

                                // Trừ kho hãng
                                PartInventory oemInventory = partInventoryRepository
                                                .findByPartAndServiceCenter(part, null)
                                                .orElseThrow(() -> new IllegalArgumentException(
                                                                "Part " + part.getCode()
                                                                                + " not found in OEM warehouse"));
                                if (oemInventory.getQuantity() < approvedQty) {
                                        throw new IllegalArgumentException(
                                                        "Insufficient stock for part " + part.getCode()
                                                                        + " in OEM warehouse");
                                }
                                oemInventory.setQuantity(oemInventory.getQuantity() - approvedQty);
                                partInventoryRepository.save(oemInventory);

                                // Cộng kho SC
                                ServiceCenter sc = supply.getServiceCenter();
                                PartInventory scInventory = partInventoryRepository.findByPartAndServiceCenter(part, sc)
                                                .orElse(PartInventory.builder()
                                                                .part(part)
                                                                .serviceCenter(sc)
                                                                .quantity(0)
                                                                .build());
                                scInventory.setQuantity(scInventory.getQuantity() + approvedQty);
                                partInventoryRepository.save(scInventory);
                        }

                        supply.setStatus(PartSupply.Status.APPROVED);

                } else if ("REJECT".equalsIgnoreCase(request.getAction())) {
                        supply.setStatus(PartSupply.Status.REJECTED);
                } else {
                        throw new IllegalArgumentException("Invalid action. Must be APPROVE or REJECT");
                }

                supply.setNote(request.getNote());
                partSupplyRepository.save(supply);

                List<PartRequestDetailResponse> details = partRequestDetailRepository.findByPartRequest(supply)
                                .stream()
                                .map(d -> PartRequestDetailResponse.builder()
                                                .partName(d.getPart().getName())
                                                .requestedQuantity(d.getRequestedQuantity())
                                                .approvedQuantity(d.getApprovedQuantity())
                                                .remark(d.getRemark())
                                                .build())
                                .toList();

                return PartSupplyDetailResponse.builder()
                                .id(supply.getId())
                                .serviceCenterName(supply.getServiceCenter().getName())
                                .createdBy(supply.getCreatedBy().getName())
                                .createdDate(supply.getCreatedDate())
                                .status(supply.getStatus().name())
                                .note(supply.getNote())
                                .details(details)
                                .build();
        }

}