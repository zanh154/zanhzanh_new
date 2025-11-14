package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllPartResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartCategoryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartListResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ModelPart;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Part;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Vehicle;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ModelPartRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehicleRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PartService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartServiceImpl implements PartService {
        final PartRepository partRepository;
        final VehicleRepository vehicleRepository;
        final ModelPartRepository modelPartRepository;

        @Override
        public PartCategoryResponse handleListCategory(String vin) {
                // 1. Lấy xe theo VIN
                Vehicle vehicle = vehicleRepository.findByVin(vin)
                                .orElseThrow(() -> new NoSuchElementException("Vehicle not found with VIN: " + vin));

                // 2. Lấy tất cả Part liên quan đến model của xe
                List<ModelPart> modelParts = modelPartRepository.findByModelId(vehicle.getModel().getId());

                // 3. Lấy tất cả category duy nhất từ danh sách Part này
                Set<String> categorySet = modelParts.stream()
                                .map(mp -> mp.getPart().getPartCategory())
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet());

                boolean status = !categorySet.isEmpty();

                return PartCategoryResponse.builder()
                                .category(categorySet)
                                .status(status)
                                .build();
        }

        @Override
        public PartListResponse handlePartList(String category, String vin) {
                // 1. Lấy xe theo VIN
                Vehicle vehicle = vehicleRepository.findByVin(vin)
                                .orElseThrow(() -> new NoSuchElementException("Vehicle not found with VIN: " + vin));

                // 2. Lấy danh sách Part liên quan đến model và category từ ModelPart
                List<ModelPart> modelParts = modelPartRepository
                                .findByModelIdAndPartPartCategoryIgnoreCase(vehicle.getModel().getId(), category);

                // 3. Chuyển sang response
                Set<PartResponse> responseList = modelParts.stream()
                                .map(ModelPart::getPart)
                                .map(p -> PartResponse.builder()
                                                .id(p.getId())
                                                .name(p.getName())
                                                .description(p.getDescription())
                                                .partCategory(p.getPartCategory())
                                                .build())
                                .collect(Collectors.toSet());

                return PartListResponse.builder()
                                .partList(responseList)
                                .build();
        }

        @Override
        public GetAllPartResponse handleGetPartList() {
                List<Part> partList = this.partRepository.findAll();
                List<PartResponse> responseList = new ArrayList<>();
                for (Part p : partList) {
                        PartResponse pr = PartResponse.builder()
                                        .id(p.getId())
                                        .description(p.getDescription())
                                        .code(p.getCode())
                                        .name(p.getName())
                                        .partCategory(p.getPartCategory())
                                        .build();
                        responseList.add(pr);
                }
                return GetAllPartResponse.builder()
                                .partList(responseList)
                                .build();
        }

}
