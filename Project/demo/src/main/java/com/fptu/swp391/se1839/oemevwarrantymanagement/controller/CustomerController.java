package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.AddVehicleRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CustomerRegisterRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.CustomerRegisterResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.CustomerSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RegisteredVehicleResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.VehicleInfoResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.CustomerService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class CustomerController {

        final CustomerService customerService;

        @PostMapping("/customers")
        @PreAuthorize("hasAnyAuthority('SC_STAFF')")
        public ResponseEntity<ApiResponse<CustomerRegisterResponse>> registerCustomer(
                        @Valid @RequestBody CustomerRegisterRequest req,
                        @AuthenticationPrincipal Jwt jwt) {
                Long id = Long.valueOf(jwt.getClaim("userId").toString());
                Long scId = Long.valueOf(jwt.getClaim("serviceCenterId").toString());
                CustomerRegisterResponse customer = customerService.registerCustomer(req, id, scId);
                var result = ApiResponse.<CustomerRegisterResponse>builder()
                                .status(HttpStatus.CREATED.toString())
                                .message("Create customer successfully")
                                .data(customer)
                                .build();
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }

        @GetMapping("/customer")
        public ResponseEntity<ApiResponse<CustomerRegisterResponse>> findRegisteredVehicleByPhone(
                        @RequestParam String vin) {

                CustomerRegisterResponse customerResponse = customerService.handleFindCustomerByVin(vin);

                var result = ApiResponse.<CustomerRegisterResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get vehicle successfully")
                                .data(customerResponse)
                                .build();
                return ResponseEntity.ok(result);
        }

        @PutMapping("/customers/{id}")
        @PreAuthorize("hasAnyAuthority('SC_STAFF')")
        public ResponseEntity<ApiResponse<CustomerRegisterResponse>> updateCustomer(
                        @PathVariable Long id,
                        @Valid @RequestBody CustomerRegisterRequest req) {

                var updated = customerService.updateCustomer(id, req);
                var result = ApiResponse.<CustomerRegisterResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Update customer successfully")
                                .data(updated)
                                .build();
                return ResponseEntity.ok(result);
        }

        @PostMapping("/customers/{id}/vehicles")
        @PreAuthorize("hasAnyAuthority('SC_STAFF')")
        public ResponseEntity<ApiResponse<CustomerRegisterResponse>> addVehicleForExistingCustomer(
                        @PathVariable Long id,
                        @Valid @RequestBody AddVehicleRequest req) {

                CustomerRegisterResponse response = customerService.addVehicleForExistingCustomer(id, req);

                var result = ApiResponse.<CustomerRegisterResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Add vehicle for existing customer successfully")
                                .data(response)
                                .build();

                return ResponseEntity.ok(result);
        }

        @GetMapping("/vehicles/registered")
        @PreAuthorize("hasAnyAuthority('SC_STAFF', 'ADMIN')")
        public ResponseEntity<ApiResponse<List<RegisteredVehicleResponse>>> getAllRegisteredVehicles() {
                var data = customerService.getAllRegisteredVehicles();
                var result = ApiResponse.<List<RegisteredVehicleResponse>>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get all registered vehicles successfully")
                                .data(data)
                                .build();
                return ResponseEntity.ok(result);
        }

        @GetMapping("/customers")
        @PreAuthorize("hasAnyAuthority('SC_STAFF', 'ADMIN')")
        public ResponseEntity<ApiResponse<List<CustomerSummaryResponse>>> getCustomerSummary() {
                var data = customerService.getAllCustomerSummaries();
                var result = ApiResponse.<List<CustomerSummaryResponse>>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get customer summary successfully")
                                .data(data)
                                .build();
                return ResponseEntity.ok(result);
        }

        @GetMapping("/{customerId}/vehicles")
        public ResponseEntity<ApiResponse<List<VehicleInfoResponse>>> getVehiclesByCustomerId(
                        @PathVariable Long customerId) {
                List<VehicleInfoResponse> vehicles = customerService.getVehiclesByCustomerId(customerId);
                var result = ApiResponse.<List<VehicleInfoResponse>>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get customer summary successfully")
                                .data(vehicles)
                                .build();
                return ResponseEntity.ok(result);

        }

        @GetMapping("/vehicles/find")
        @PreAuthorize("hasAnyAuthority('SC_STAFF', 'ADMIN')")
        public ResponseEntity<ApiResponse<List<CustomerSummaryResponse>>> findByKey(
                        @RequestParam String key) {

                List<CustomerSummaryResponse> data = customerService.findByKey(key);

                var result = ApiResponse.<List<CustomerSummaryResponse>>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Find registered vehicles successfully")
                                .data(data)
                                .build();

                return ResponseEntity.ok(result);
        }
}
