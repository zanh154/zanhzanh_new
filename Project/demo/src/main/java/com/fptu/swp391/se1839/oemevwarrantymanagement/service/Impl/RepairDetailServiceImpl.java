package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllRepairDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RepairDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartClaim;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairDetail;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairDetailRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairOrderRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairOrderVerificationRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairStepRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehiclePartRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.RepairDetailService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairDetailServiceImpl implements RepairDetailService {
        final RepairDetailRepository repairDetailRepository;

        RepairDetailResponse buildRepairDetailResponse(RepairDetail rd) {
                long quantity = rd.getRepairOrder().getWarrantyClaim().getPartClaims().stream()
                                .filter(pc -> pc.getPart().getId().equals(rd.getPart().getId()))
                                .mapToLong(PartClaim::getQuantity)
                                .sum();

                RepairDetailResponse.RepairDetailResponseBuilder builder = RepairDetailResponse.builder()
                                .id(rd.getId())
                                .partName(rd.getPart().getName())
                                .category(rd.getPart().getPartCategory())
                                .oldSerialNumber(rd.getVehiclePart().getOldSerialNumber())
                                .quantity(quantity)
                                .productYear(rd.getVehiclePart().getVehicle().getProductYear())
                                .modelName(rd.getVehiclePart().getVehicle().getModel().getName())
                                .vin(rd.getVehiclePart().getVehicle().getVin())
                                .licensePlate(rd.getVehiclePart().getVehicle().getLicensePlate());

                // --- Kiểm tra step Assembly ---
                boolean hasAssemblyStep = rd.getRepairOrder().getSteps().stream()
                                .anyMatch(s -> "Assembly".equalsIgnoreCase(s.getTitle()));

                // Nếu trạng thái là REPLACED và có step Assembly, thêm các field đặc biệt
                if (rd.getStatus() == RepairDetail.DetailStatus.REPLACED && hasAssemblyStep) {
                        builder.installationDate(rd.getVehiclePart().getInstallationDate())
                                        .replacementDescription(rd.getDescription())
                                        .technicianName(rd.getRepairOrder().getTechnical().getName())
                                        .newSerialNumber(rd.getVehiclePart().getNewSerialNumber()); // nếu có field
                                                                                                    // riêng
                }

                return builder.build();
        }

        public GetAllRepairDetailResponse handleGetRepairDetail(long repairOrderId) {
                List<RepairDetail> details = repairDetailRepository.findByRepairOrderId(repairOrderId)
                                .stream()
                                .filter(rd -> rd.getStatus() != RepairDetail.DetailStatus.REJECTED) // loại bỏ REJECTED
                                .toList();

                List<RepairDetailResponse> responses = details.stream()
                                .map(rd -> buildRepairDetailResponse(rd))
                                .toList();

                return new GetAllRepairDetailResponse(responses);
        }

}
