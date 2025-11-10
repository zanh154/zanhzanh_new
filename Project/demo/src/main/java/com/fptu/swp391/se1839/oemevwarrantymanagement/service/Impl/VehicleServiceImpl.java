package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.VehicleRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllVehicleResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetRegisteredVehicleResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetVehicleResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.CampaignVehicle;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Customer;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ServiceCampaign;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Vehicle;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.CustomerRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehicleRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.VehicleService;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleServiceImpl implements VehicleService {
        final VehicleRepository vehicleRepository;
        final CustomerRepository customerRepository;

        @Override
        public GetAllVehicleResponse getAllVehicles() {
                List<Vehicle> vehicles = vehicleRepository.findAll();

                List<GetVehicleResponse> getVehicleResponses = vehicles.stream()
                                .map(v -> GetVehicleResponse.builder()
                                                .vin(v.getVin())
                                                .licensePlate(v.getLicensePlate())
						.modelName(v.getModel().getName())
                                                .productYear(v.getProductYear())
                                                .customerName(v.getCustomer() != null ? v.getCustomer().getName()
                                                                : "N/A")
                                                .build())
                                .collect(Collectors.toList());

                return GetAllVehicleResponse.builder()
                                .vehicles(getVehicleResponses)
                                .build();
        }

        @Override
        public List<GetRegisteredVehicleResponse> handleFindRegisteredVehicleByPhone(VehicleRequest request) {
                Customer customer = customerRepository.findByPhoneNumber(request.getPhone())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Customer with phone " + request.getPhone() + " not found"));

                Set<Vehicle> vehicles = customer.getVehicles();
                List<GetRegisteredVehicleResponse> responses = new ArrayList<>();
                LocalDate now = LocalDate.now();

                for (Vehicle vehicle : vehicles) {
                        Set<CampaignVehicle> campaignVehicles = vehicle.getCampaignVehicles();

                        // ✅ Xe không có campaign nào
                        if (campaignVehicles.isEmpty()) {
                                responses.add(GetRegisteredVehicleResponse.builder()
                                                .vehicle(toGetVehicleResponse(vehicle))
                                                .build());
                                continue;
                        }

                        // ✅ Có campaign -> kiểm tra thời gian
                        boolean matched = false;
                        for (CampaignVehicle cv : campaignVehicles) {
                                ServiceCampaign campaign = cv.getServiceCampaign();
                                LocalDate start = campaign.getStartDate();
                                LocalDate end = campaign.getEndDate().plusDays(7);

                                if (!now.isBefore(start) && !now.isAfter(end)) {
                                        // now nằm trong khoảng hợp lệ → hiển thị campaign
                                        responses.add(GetRegisteredVehicleResponse.builder()
                                                        .vehicle(toGetVehicleResponse(vehicle))
                                                        .code(campaign.getCode())
                                                        .name(campaign.getName())
                                                        .description(campaign.getDescription())
                                                        .startDate(campaign.getStartDate())
                                                        .endDate(campaign.getEndDate())
                                                        .status(GetRegisteredVehicleResponse.CampaignVehicleStatus
                                                                        .valueOf(cv.getStatus().name()))
                                                        .build());
                                        matched = true;
                                        break;
                                }
                        }

                        // ✅ Nếu không có campaign nào trong khoảng thời gian → hiển thị xe, các trường
                        // campaign = null
                        if (!matched) {
                                responses.add(GetRegisteredVehicleResponse.builder()
                                                .vehicle(toGetVehicleResponse(vehicle))
                                                .build());
                        }
                }

                return responses.isEmpty() ? null : responses;
        }

        private GetVehicleResponse toGetVehicleResponse(Vehicle vehicle) {
                return GetVehicleResponse.builder()
                                .vin(vehicle.getVin())
                                .licensePlate(vehicle.getLicensePlate())
                                .build();
        }

        @Override
        public GetAllVehicleResponse handleFindVehicleByPhone(String phone) {
                List<Vehicle> vehicles = vehicleRepository.findByCustomerPhone(phone);

                List<GetVehicleResponse> getVehicleResponses = vehicles.stream()
                                .map(v -> GetVehicleResponse.builder()
                                                .vin(v.getVin())
                                                .licensePlate(v.getLicensePlate())
                                                .modelName(v.getModel().getName())
                                                .productYear(v.getProductYear())
                                                .customerName(v.getCustomer() != null ? v.getCustomer().getName()
                                                                : "N/A")
                                                .build())
                                .collect(Collectors.toList());

                return GetAllVehicleResponse.builder()
                                .vehicles(getVehicleResponses)
                                .build();
        }

}