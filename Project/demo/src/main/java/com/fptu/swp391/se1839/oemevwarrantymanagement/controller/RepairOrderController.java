package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.ChooseTechnicalRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.FilterRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.RepairOrderVerificationRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ChooseTechnicalResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.OrderDashboardResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.OrderDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RepairHistoryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RepairOrderVerificationResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.RepairOrderService;

import org.springframework.http.MediaType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.oauth2.jwt.Jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class RepairOrderController {
        final RepairOrderService repairOrderService;

        @GetMapping("/repair-orders")
        public ResponseEntity<ApiResponse<OrderDashboardResponse>> getRepairOrder(
                        @AuthenticationPrincipal Jwt jwt,
                        @RequestBody(required = false) FilterRequest request) {

                Object scClaim = jwt.getClaim("serviceCenterId");
                Long serviceCenterId = (scClaim != null) ? Long.parseLong(scClaim.toString()) : 0L;

                if (request == null) {
                        request = new FilterRequest();
                }

                Long userId = Long.parseLong(jwt.getClaim("userId").toString());
                OrderDashboardResponse odr = this.repairOrderService.handleOrderDashboard(serviceCenterId, request,
                                userId);

                var result = ApiResponse.<OrderDashboardResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get inf dashboard successfully")
                                .data(odr)
                                .build();

                return ResponseEntity.ok(result);
        }

        @GetMapping("/repair-orders/{id}")
        public ResponseEntity<ApiResponse<OrderDetailResponse>> getRepairOrderDetail(
                        @AuthenticationPrincipal Jwt jwt,
                        @PathVariable("id") long orderId) throws Exception {
                Object scClaim = jwt.getClaim("serviceCenterId");
                Long serviceCenterId = (scClaim != null) ? Long.parseLong(scClaim.toString()) : 0L;

                OrderDetailResponse orderDetail = this.repairOrderService.handleGetDetailOrder(serviceCenterId,
                                orderId);
                var result = ApiResponse.<OrderDetailResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get inf dashboard successfully")
                                .data(orderDetail)
                                .build();
                return ResponseEntity.ok(result);
        }

        @PutMapping("/repair-orders/{id}")
        public ResponseEntity<ApiResponse<ChooseTechnicalResponse>> chooseTechinician(
                        @PathVariable("id") Long repairOrderId, @RequestBody ChooseTechnicalRequest requets) {
                ChooseTechnicalResponse ctr = this.repairOrderService.handleChooseTechnical(repairOrderId, requets);
                var result = ApiResponse.<ChooseTechnicalResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Choose techinician successfully")
                                .data(ctr)
                                .build();
                return ResponseEntity.ok(result);
        }

        @GetMapping("/{id}/send-complete-email")
        public ResponseEntity<ApiResponse<String>> sendRepairCompletedEmail(@PathVariable Long id) {
                String success = repairOrderService.sendRepairCompletedEmail(id);
                var result = ApiResponse.<String>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Choose techinician successfully")
                                .data(success)
                                .build();
                return ResponseEntity.ok(result);
        }

        @PostMapping(value = "/{id}/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ApiResponse<RepairOrderVerificationResponse>> verifyRepairOrder(
                        @PathVariable("id") Long repairOrderId,
                        @RequestPart(value = "attachments", required = false) MultipartFile[] attachments,
                        @RequestPart("verify") RepairOrderVerificationRequest request,
                        @AuthenticationPrincipal Jwt jwt) throws IOException {
                Long userId = Long.parseLong(jwt.getClaim("userId").toString());
                RepairOrderVerificationResponse response = repairOrderService.verifyRepairOrder(repairOrderId, request,
                                userId,
                                attachments);
                var result = ApiResponse.<RepairOrderVerificationResponse>builder()
                                .status(HttpStatus.CREATED.toString())
                                .message("Verify repair order successfully")
                                .data(response)
                                .build();
                return ResponseEntity.ok(result);
        }

        @GetMapping("/repair-orders/history/{vin}")
        public ResponseEntity<ApiResponse<List<RepairHistoryResponse>>> getRepairHistoryByVin(
                        @PathVariable("vin") String vin) {

                // Gọi service lấy danh sách 4 repair order gần nhất
                List<RepairHistoryResponse> history = repairOrderService.getRecentRepairHistoryByVin(vin);

                // Đóng gói API response
                var result = ApiResponse.<List<RepairHistoryResponse>>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Recent repair history fetched successfully")
                                .data(history)
                                .build();

                return ResponseEntity.ok(result);
        }
}