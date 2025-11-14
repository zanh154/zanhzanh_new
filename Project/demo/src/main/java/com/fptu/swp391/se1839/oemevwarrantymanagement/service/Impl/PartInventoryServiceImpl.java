package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartInventoryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartInventory;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartInventoryRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PartInventoryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartInventoryServiceImpl implements PartInventoryService {

        private final PartInventoryRepository partInventoryRepository;

        @Override
        public List<PartInventoryResponse> getAllPartInventories() {
                return partInventoryRepository.findAll()
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Override
        public List<PartInventoryResponse> getPartInventoriesByServiceCenter(Long serviceCenterId) {
                return partInventoryRepository.findByServiceCenterId(serviceCenterId)
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        public int handleCalculatePartAvailability(long serviceCenterId) {
                boolean hasSpecificCenter = serviceCenterId > 0;

                long totalPartsRequested = hasSpecificCenter
                                ? partInventoryRepository.countByServiceCenterId(serviceCenterId)
                                : partInventoryRepository.countByServiceCenterIdIsNull(); // chỉ lấy những bản ghi
                                                                                          // serviceCenterId là null

                long availableParts = hasSpecificCenter
                                ? partInventoryRepository.countByServiceCenterIdAndQuantityGreaterThan(serviceCenterId,
                                                0)
                                : partInventoryRepository.countByServiceCenterIdIsNullAndQuantityGreaterThan(0);

                return totalPartsRequested == 0
                                ? 100
                                : (int) Math.round((availableParts * 100.0) / totalPartsRequested);
        }

        public int countLowStockParts(long serviceCenterId) {
                boolean hasSpecificCenter = serviceCenterId > 0;

                List<PartInventory> parts = hasSpecificCenter
                                ? partInventoryRepository.findByServiceCenterId(serviceCenterId)
                                : partInventoryRepository.findAll();

                double avgQuantity = parts.stream()
                                .mapToLong(PartInventory::getQuantity)
                                .average()
                                .orElse(0);

                int lowStockThreshold = (int) Math.ceil(avgQuantity * 0.2);

                long count = parts.stream()
                                .filter(pi -> pi.getQuantity() <= lowStockThreshold)
                                .count();

                return (int) count;
        }

        private PartInventoryResponse mapToResponse(PartInventory inventory) {
                boolean isOemWarehouse = (inventory.getServiceCenter() == null);

                return PartInventoryResponse.builder()
                                .id(inventory.getId())
                                .partId(inventory.getPart().getId())
                                .partCode(inventory.getPart().getCode())
                                .partName(inventory.getPart().getName())
                                .partCategory(inventory.getPart().getPartCategory())
                                .serviceCenterId(isOemWarehouse ? null : inventory.getServiceCenter().getId())
                                .serviceCenterName(isOemWarehouse ? "OEM Warehouse"
                                                : inventory.getServiceCenter().getName())
                                .serviceCenterAddress(isOemWarehouse ? "Main OEM Stock"
                                                : inventory.getServiceCenter().getAddress())
                                .quantity(inventory.getQuantity())
                                .unit(inventory.getPart().getUnit().name())
                                .build();
        }

}