package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.util.List;
import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllRepairDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RepairDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartClaim;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairDetail;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.VehiclePart;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairDetailRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehiclePartRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.RepairDetailService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.WarrantyClaimService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairDetailServiceImpl implements RepairDetailService {
        final RepairDetailRepository repairDetailRepository;
        final VehiclePartRepository vehiclePartRepository;

        RepairDetailResponse buildRepairDetailResponse(RepairDetail rd) {

                List<VehiclePart> vehicleParts = vehiclePartRepository.findActiveByVehicleVinAndPartId(
                                rd.getRepairOrder().getWarrantyClaim().getVehicle().getVin(),
                                rd.getPart().getId());

                List<String> oldSerialNumbers = vehicleParts.stream()
                                .map(vp -> vp.getOldSerialNumber() != null ? vp.getOldSerialNumber() : "N/A")
                                .toList();

                VehiclePart vp = rd.getVehiclePart(); // có thể null
                String oldSerialNumber = (vp != null && vp.getOldSerialNumber() != null)
                                ? vp.getOldSerialNumber()
                                : "N/A";

                RepairDetailResponse.RepairDetailResponseBuilder builder = RepairDetailResponse.builder()
                                .id(rd.getId())
                                .partName(rd.getPart().getName())
                                .category(rd.getPart().getPartCategory())
                                .oldSerialNumber(oldSerialNumber)
                                .quantity(1)
                                .productYear(rd.getRepairOrder().getWarrantyClaim().getVehicle().getProductYear())
                                .modelName(rd.getRepairOrder().getWarrantyClaim().getVehicle().getModel().getName())
                                .vin(rd.getRepairOrder().getWarrantyClaim().getVehicle().getVin())
                                .licensePlate(rd.getRepairOrder().getWarrantyClaim().getVehicle().getLicensePlate())
                                .listOldSerialNumber(oldSerialNumbers);

                // --- Kiểm tra step Assembly ---
                boolean hasAssemblyStep = rd.getRepairOrder().getSteps().stream()
                                .anyMatch(s -> "Assembly".equalsIgnoreCase(s.getTitle()));

                // Nếu trạng thái là REPLACED và có step Assembly, thêm các field đặc biệt
                if (rd.getStatus() == RepairDetail.DetailStatus.REPLACED && hasAssemblyStep && vp != null) {
                        builder.installationDate(vp.getInstallationDate())
                                        .replacementDescription(rd.getDescription())
                                        .technicianName(
                                                        rd.getRepairOrder().getTechnical() != null
                                                                        ? rd.getRepairOrder().getTechnical().getName()
                                                                        : "N/A")
                                        .newSerialNumber(vp.getNewSerialNumber() != null ? vp.getNewSerialNumber()
                                                        : "N/A");
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

        @Override
        public void updateRepairDetail(long repairDetailId, String oldSerialNumber) {
                System.out.println("oldSerialNumber = " + oldSerialNumber);

                RepairDetail rd = repairDetailRepository.findById(repairDetailId)
                                .orElseThrow(() -> new RuntimeException("Repair Detail not found"));

                VehiclePart vp = vehiclePartRepository.findBySerial(oldSerialNumber.trim())
                                .orElseThrow(() -> new RuntimeException("Vehicle Part not found"));

                rd.setVehiclePart(vp);
                repairDetailRepository.save(rd);
        }

}
