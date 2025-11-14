package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CreateCampaignRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.UpdateCampaignRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllVehicleCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ServiceCampaignDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ServiceCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ServiceCampaignSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.VehicleInCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.CampaignService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class ServiceCampaignController {
        final CampaignService serviceCampaignService;

        @GetMapping("/campaigns")
        @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
        public ResponseEntity<ApiResponse<GetAllCampaignResponse>> getAllCampaigns(
                        @AuthenticationPrincipal Jwt jwt) {

                GetAllCampaignResponse campaigns = serviceCampaignService.handleGetAllCampaigns();
                var result = ApiResponse.<GetAllCampaignResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get all campaigns successfully")
                                .data(campaigns)
                                .build();

                return ResponseEntity.ok(result);
        }

        @GetMapping("/campaigns/{id}")
        @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
        public ResponseEntity<ApiResponse<ServiceCampaignDetailResponse>> getCampaignDetail(
                        @PathVariable Long id,
                        @AuthenticationPrincipal Jwt jwt) {

                ServiceCampaignDetailResponse detail = serviceCampaignService.handleGetCampaignById(id);
                var result = ApiResponse.<ServiceCampaignDetailResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get campaign detail successfully")
                                .data(detail)
                                .build();

                return ResponseEntity.ok(result);
        }

        @PostMapping("/campaigns")
        @PreAuthorize("hasAuthority('ADMIN')")
        public ResponseEntity<ApiResponse<ServiceCampaignResponse>> createCampaign(
                        @Validated @RequestBody CreateCampaignRequest request,
                        @AuthenticationPrincipal Jwt jwt) {

                ServiceCampaignResponse response = serviceCampaignService.handleCreateCampaign(request);
                var result = ApiResponse.<ServiceCampaignResponse>builder()
                                .status(HttpStatus.CREATED.toString())
                                .message("Campaign created successfully")
                                .data(response)
                                .build();

                return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }

        @DeleteMapping("/campaigns/{id}")
        @PreAuthorize("hasAuthority('ADMIN')")
        public ResponseEntity<ApiResponse<Void>> deleteCampaign(
                        @PathVariable Long id,
                        @AuthenticationPrincipal Jwt jwt) {

                serviceCampaignService.handleDeleteCampaign(id);
                var result = ApiResponse.<Void>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Campaign deleted successfully")
                                .build();

                return ResponseEntity.ok(result);
        }

        @PutMapping("/campaigns/{id}")
        public ResponseEntity<ServiceCampaignResponse> updateCampaign(
                        @RequestBody UpdateCampaignRequest request,
                        @PathVariable Long id,
                        @AuthenticationPrincipal Jwt jwt) {
                ServiceCampaignResponse response = serviceCampaignService.handleUpdateCampaign(request, id);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/campaign")
        @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
        public ResponseEntity<ApiResponse<List<ServiceCampaignSummaryResponse>>> getCampaignByVin(@RequestParam String vin, @AuthenticationPrincipal Jwt jwt) {

        List<ServiceCampaignSummaryResponse> detail = serviceCampaignService.handleGetCampaignByVin(vin);

        var result = ApiResponse.<List<ServiceCampaignSummaryResponse>>builder()
                .status(HttpStatus.OK.toString())
                .message("Get campaign detail successfully")
                .data(detail)
                .build();

        return ResponseEntity.ok(result);
        }


        @GetMapping("/campaigns/vehicles")
        @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
        public ResponseEntity<ApiResponse<GetAllVehicleCampaignResponse>> getAllVehiclesWithCampaigns(
                        @AuthenticationPrincipal Jwt jwt) {

                GetAllVehicleCampaignResponse response = serviceCampaignService.handleGetAllVehiclesWithCampaigns();

                var result = ApiResponse.<GetAllVehicleCampaignResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get all vehicles with campaigns successfully")
                                .data(response)
                                .build();

                return ResponseEntity.ok(result);
        }

        @PostMapping("/campaigns/{id}/notify")
        @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
        public ResponseEntity<ApiResponse<String>> notifyCustomers(
                @PathVariable Long id,
                @AuthenticationPrincipal Jwt jwt) {

        String resultMessage = serviceCampaignService.notifyCustomersByCampaign(id);

        var result = ApiResponse.<String>builder()
                .status(HttpStatus.OK.toString())
                .message(resultMessage)
                .build();

        return ResponseEntity.ok(result);
        }

	@GetMapping("/campaigns/vehicles/by-sc")
        @PreAuthorize("hasAnyAuthority('ADMIN','SC_STAFF')")
        public ResponseEntity<ApiResponse<List<VehicleInCampaignResponse>>> getVehiclesByServiceCenter(
                @AuthenticationPrincipal Jwt jwt) {

        Long scId = Long.valueOf(jwt.getClaimAsString("serviceCenterId"));

        List<VehicleInCampaignResponse> vehicles = serviceCampaignService.handleGetVehiclesInCampaignByServiceCenter(scId);

        var result = ApiResponse.<List<VehicleInCampaignResponse>>builder()
                .status(HttpStatus.OK.toString())
                .message("Get vehicles in campaigns by service center successfully")
                .data(vehicles)
                .build();

        return ResponseEntity.ok(result);
        }

}