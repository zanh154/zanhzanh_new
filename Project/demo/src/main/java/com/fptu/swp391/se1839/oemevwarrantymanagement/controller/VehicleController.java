package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.VehicleRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllVehicleResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetRegisteredVehicleResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.VehicleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class VehicleController {
        final VehicleService vehicleService;

        @GetMapping("/claims/vehicle/{phone}")
        public ResponseEntity<ApiResponse<List<GetRegisteredVehicleResponse>>> findRegisteredVehicleByPhone(
                        @PathVariable("phone") String phone) {

                List<GetRegisteredVehicleResponse> vehicleResponse = vehicleService
                                .handleFindRegisteredVehicleByPhone(new VehicleRequest(phone));

                var result = ApiResponse.<List<GetRegisteredVehicleResponse>>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get vehicle successfully")
                                .data(vehicleResponse)
                                .build();
                return ResponseEntity.ok(result);
        }

        @GetMapping("/vehicles")
        public ResponseEntity<ApiResponse<GetAllVehicleResponse>> getAllVehicles() {
                GetAllVehicleResponse vehicles = vehicleService.getAllVehicles();
                var result = ApiResponse.<GetAllVehicleResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get all vehicles successfully")
                                .data(vehicles)
                                .build();
                return ResponseEntity.ok(result);
        }

        @GetMapping("/vehicle")
        public ResponseEntity<ApiResponse<GetAllVehicleResponse>> findVehicleByPhone(
                        @RequestParam String phone) {

                GetAllVehicleResponse vehicleResponse = vehicleService
                                .handleFindVehicleByPhone(phone);

                var result = ApiResponse.<GetAllVehicleResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get vehicle successfully")
                                .data(vehicleResponse)
                                .build();
                return ResponseEntity.ok(result);
        }

}