package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;

import com.cloudinary.utils.ObjectUtils;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CreateClaimRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.FilterRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.PartClaimRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.WarrantyClaimStatusRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ClaimDashboardResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ClaimDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ClaimSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ClaimsByPriorityResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ComponentCostSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.CostAnalysisResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.CreateClaimResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.DashboardClaimSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.DecodeImageReponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.FilterClaimResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetPartClaimResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ModelFailureResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.MonthlyCostSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartQuantityResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.SummaryClaimResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.SummaryItemResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.WarrantyClaimStatusResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.CampaignVehicle;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Customer;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Model;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Part;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartClaim;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartInventory;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartPolicy;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartPriceHistory;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairDetail;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairManual;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairOrder;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairStep;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ServiceCampaign;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ServiceCenter;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Vehicle;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.VehiclePart;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.WarrantyClaim;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.WarrantyPolicy;
import com.fptu.swp391.se1839.oemevwarrantymanagement.event.EntityCreatedEvent;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.CampaignVehicleRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.CustomerRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ModelRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartClaimRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartPriceHistoryRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.PartRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairDetailRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairManualRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairOrderRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairStepRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.SCExpenseRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ServiceCenterRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.UserRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehiclePartRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehicleRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.WarrantyClaimRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.WarrantyClaimService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarrantyClaimServiceImpl implements WarrantyClaimService {

        final UserRepository userRepository;
        final CustomerRepository customerRepository;
        final ModelRepository modelRepository;
        final WarrantyClaimRepository warrantyClaimRepository;
        final PartRepository partRepository;
        final VehicleRepository vehicleRepository;
        final ServiceCenterRepository serviceCenterRepository;
        final PartPriceHistoryRepository partPriceHistoryRepository;
        final CampaignVehicleRepository campaignVehicleRepository;
        final PartClaimRepository partClaimRepository;
        final SCExpenseRepository scExpenseReposiotry;
        final RepairDetailRepository repairDetailRepository;
        final RepairStepRepository repairStepRepository;
        final ApplicationEventPublisher applicationEventPublisher;
        final RepairOrderRepository repairOrderRepository;
        final VehiclePartRepository vehiclePartRepository;
        final RepairManualRepository repairManualRepository;
        final SimpMessagingTemplate messagingTemplate;
        final CloudinaryServiceImpl cloudinaryService;
        final Cloudinary cloudinary;

        @PersistenceContext
        private EntityManager entityManager;
        // ================= Dashboard & Summary =================

        public DashboardClaimSummaryResponse handleSummaryClaims(Long serviceCenterId) {
                long count;
                long emergencyCount;

                if (serviceCenterId == null || serviceCenterId == 0) {
                        // EVM Staff: xem tất cả claim
                        count = warrantyClaimRepository.countAllClaims(); // <--- viết lại query tổng
                        emergencyCount = warrantyClaimRepository.countAllByPriority(WarrantyClaim.ClaimPriority.HIGH);
                } else {
                        // SC Staff: xem claim theo trung tâm
                        count = warrantyClaimRepository.countByServiceCenterId(serviceCenterId);
                        emergencyCount = warrantyClaimRepository.countByServiceCenterIdAndPriority(
                                        serviceCenterId, WarrantyClaim.ClaimPriority.HIGH);
                }

                System.out.println("Total claims: " + count + ", High: " + emergencyCount);

                return DashboardClaimSummaryResponse.builder()
                                .count(count)
                                .emegency((int) emergencyCount)
                                .build();
        }

        Long getAllClaims(Long serviceCenterId, User user) {
                List<WarrantyClaim.ClaimStatus> allowedStatuses = List.of(
                                WarrantyClaim.ClaimStatus.PENDING,
                                WarrantyClaim.ClaimStatus.COMPLETED,
                                WarrantyClaim.ClaimStatus.APPROVED,
                                WarrantyClaim.ClaimStatus.REJECTED);

                boolean isEvmStaff = user.getRole() == User.Role.EVM_STAFF;
                boolean hasSpecificCenter = serviceCenterId != null && serviceCenterId > 0;

                if (hasSpecificCenter) {
                        return isEvmStaff
                                        ? warrantyClaimRepository.countClaimsByServiceCenterAndStatuses(serviceCenterId,
                                                        allowedStatuses)
                                        : warrantyClaimRepository.countByServiceCenterId(serviceCenterId);
                } else {
                        return isEvmStaff
                                        ? warrantyClaimRepository.countClaimsByStatuses(allowedStatuses)
                                        : warrantyClaimRepository.count();
                }
        }

        Long getClaimsByStatus(Long serviceCenterId, WarrantyClaim.ClaimStatus status, User user) {

                boolean isEvmStaff = user.getRole() == User.Role.EVM_STAFF;
                boolean hasSpecificCenter = serviceCenterId != null && serviceCenterId > 0;

                if (hasSpecificCenter) {
                        // EVM staff chỉ được tính các trạng thái cho phép
                        return warrantyClaimRepository.countByServiceCenterIdAndStatusIn(serviceCenterId,
                                        status);
                } else {
                        return warrantyClaimRepository.countByStatusIn(status);
                }
        }

        Long priorityHighCount(Long serviceCenterId, User user) {
                List<WarrantyClaim.ClaimStatus> allowedStatuses;

                switch (user.getRole()) {
                        case EVM_STAFF -> allowedStatuses = List.of(
                                        WarrantyClaim.ClaimStatus.PENDING,
                                        WarrantyClaim.ClaimStatus.REJECTED,
                                        WarrantyClaim.ClaimStatus.APPROVED);
                        case ADMIN -> allowedStatuses = List.of(
                                        WarrantyClaim.ClaimStatus.PENDING,
                                        WarrantyClaim.ClaimStatus.APPROVED,
                                        WarrantyClaim.ClaimStatus.COMPLETED,
                                        WarrantyClaim.ClaimStatus.DRAFT,
                                        WarrantyClaim.ClaimStatus.REJECTED);
                        case TECHNICIAN -> allowedStatuses = List.of(
                                        WarrantyClaim.ClaimStatus.DRAFT,
                                        WarrantyClaim.ClaimStatus.APPROVED,
                                        WarrantyClaim.ClaimStatus.REJECTED);
                        case SC_STAFF -> allowedStatuses = List.of(
                                        WarrantyClaim.ClaimStatus.PENDING,
                                        WarrantyClaim.ClaimStatus.APPROVED,
                                        WarrantyClaim.ClaimStatus.COMPLETED,
                                        WarrantyClaim.ClaimStatus.DRAFT,
                                        WarrantyClaim.ClaimStatus.REJECTED);

                        default -> allowedStatuses = List.of(); // role khác nếu có
                }

                boolean hasSpecificCenter = serviceCenterId != null && serviceCenterId > 0;

                if (user.getRole() == User.Role.EVM_STAFF || user.getRole() == User.Role.ADMIN
                                || user.getRole() == User.Role.TECHNICIAN) {
                        // Không cần serviceCenterId
                        return warrantyClaimRepository.countByPriorityAndStatusIn(
                                        WarrantyClaim.ClaimPriority.HIGH,
                                        allowedStatuses);
                } else if (user.getRole() == User.Role.SC_STAFF) {
                        // Phải có serviceCenterId
                        if (!hasSpecificCenter)
                                return 0L; // Nếu không có center thì trả 0
                        return warrantyClaimRepository.countByServiceCenterIdAndPriorityAndStatusIn(
                                        serviceCenterId,
                                        WarrantyClaim.ClaimPriority.HIGH,
                                        allowedStatuses);
                }

                return 0L; // Default fallback
        }

        // Lấy danh sách claim theo role của user, với serviceCenterId có thể null
        List<WarrantyClaim> getClaimsByRole(Long serviceCenterId, User user) {
                List<WarrantyClaim.ClaimStatus> allowedStatuses = List.of(
                                WarrantyClaim.ClaimStatus.PENDING,
                                WarrantyClaim.ClaimStatus.APPROVED,
                                WarrantyClaim.ClaimStatus.COMPLETED,
                                WarrantyClaim.ClaimStatus.REJECTED);

                boolean isEvmStaff = user.getRole() == User.Role.EVM_STAFF;
                boolean hasSpecificCenter = serviceCenterId != null && serviceCenterId > 0;

                if (hasSpecificCenter) {
                        return isEvmStaff
                                        ? warrantyClaimRepository.findByServiceCenterIdAndStatusIn(serviceCenterId,
                                                        allowedStatuses)
                                        : warrantyClaimRepository.findByServiceCenterId(serviceCenterId);
                } else {
                        return isEvmStaff
                                        ? warrantyClaimRepository.findByStatusIn(allowedStatuses)
                                        : warrantyClaimRepository.findAll();
                }
        }

        // Tính phần trăm claim được APPROVED
        long perCentAcceptedClaims(Long serviceCenterId, User user) {
                // Chỉ tính nếu là EVM_STAFF
                if (user.getRole() != User.Role.EVM_STAFF) {
                        return 0;
                }

                // Lấy tất cả claim đã lọc theo serviceCenterId
                List<WarrantyClaim> claims = getClaimsByRole(serviceCenterId, user);

                // Chỉ lấy claim có status PENDING, REJECTED, APPROVED
                List<WarrantyClaim> filteredClaims = claims.stream()
                                .filter(c -> c.getStatus() == WarrantyClaim.ClaimStatus.REJECTED
                                                && "EVM_STAFF".equals(c.getRejectBy())
                                                || c.getStatus() == WarrantyClaim.ClaimStatus.APPROVED)
                                .toList();

                long totalClaims = filteredClaims.size();
                if (totalClaims == 0)
                        return 0;

                // Đếm số claim APPROVED
                long approvedClaims = filteredClaims.stream()
                                .filter(c -> c.getStatus() == WarrantyClaim.ClaimStatus.APPROVED)
                                .count();

                // Tính phần trăm
                return (approvedClaims * 100) / totalClaims;
        }

        double totalEstimatedCost(Long serviceCenterId, User user, List<WarrantyClaim> allClaims) {
                // Lọc theo service center nếu có
                Stream<WarrantyClaim> filtered = allClaims.stream()
                                .filter(c -> serviceCenterId == null || serviceCenterId <= 0
                                                || c.getServiceCenter().getId() == serviceCenterId);

                switch (user.getRole()) {
                        case EVM_STAFF ->
                                filtered = filtered.filter(c -> c.getStatus() == WarrantyClaim.ClaimStatus.PENDING
                                                || c.getStatus() == WarrantyClaim.ClaimStatus.APPROVED);
                        case ADMIN ->
                                filtered = filtered.filter(c -> c.getStatus() != WarrantyClaim.ClaimStatus.REJECTED);
                        case TECHNICIAN ->
                                filtered = filtered.filter(c -> c.getStatus() == WarrantyClaim.ClaimStatus.DRAFT
                                                || c.getStatus() == WarrantyClaim.ClaimStatus.APPROVED);
                        case SC_STAFF ->
                                filtered = filtered.filter(c -> c.getStatus() != WarrantyClaim.ClaimStatus.REJECTED);
                        default -> {
                        }
                }

                return filtered.mapToDouble(this::calculateEstimatedCost)
                                .sum();
        }

        SummaryClaimResponse handleSummaryClaim(long serviceCenterId, List<WarrantyClaim> warrantyClaims,
                        User user) {
                long countAll = getAllClaims(serviceCenterId, user);
                long countInProcess = getClaimsByStatus(serviceCenterId, WarrantyClaim.ClaimStatus.PENDING, user);
                long countSuccess = getClaimsByStatus(serviceCenterId, WarrantyClaim.ClaimStatus.APPROVED, user);
                double totalEstimatedCost = totalEstimatedCost(serviceCenterId, user, warrantyClaims);
                long priorityHigh = priorityHighCount(serviceCenterId, user);

                return SummaryClaimResponse.builder()
                                .total(new SummaryItemResponse(countAll, "All claims"))
                                .pending(new SummaryItemResponse(countInProcess, "Pending claims"))
                                .approved(new SummaryItemResponse(countSuccess, "Approved claims"))
                                .cost(new SummaryItemResponse(totalEstimatedCost, "Estimated Cost"))
                                .priorityHighCount(new SummaryItemResponse(priorityHigh, "High Priority Claims"))
                                .perCentAccaptedClaims(new SummaryItemResponse(
                                                perCentAcceptedClaims(serviceCenterId, user),
                                                "Percent of Accepted Claims"))
                                .status(true)
                                .build();
        }

        Vehicle getVehicleByVin(String vin) {
                return vehicleRepository.findByVin(vin)
                                .orElseThrow(() -> new NoSuchElementException("Vehicle not found with vin " + vin));
        }

        ServiceCenter getServiceCenterById(long id) {
                return serviceCenterRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Service center not found with id " + id));
        }

        List<ServiceCenter> geServiceCenters(long id) {
                return serviceCenterRepository.findAll();
        }

        Set<String> handleGetAllStatus(WarrantyClaim claim, User.Role actor) {
                Set<String> statuses = new LinkedHashSet<>();
                switch (claim.getStatus()) {
                        case DRAFT -> {
                                if (actor == User.Role.SC_STAFF || actor == User.Role.ADMIN) {
                                        boolean allZero = claim.getPartClaims().stream()
                                                        .allMatch(partClaim -> partClaim.getQuantity() != 0);

                                        if (allZero) {
                                                statuses.add("PENDING");
                                        }
                                        statuses.add("REJECTED");
                                }
                        }
                        case REJECTED -> {
                                if (actor == User.Role.TECHNICIAN || actor == User.Role.ADMIN) {
                                        statuses.add("DRAFT");
                                }
                        }
                        case PENDING -> {
                                if (actor == User.Role.EVM_STAFF || actor == User.Role.ADMIN) {
                                        statuses.add("APPROVED");
                                        statuses.add("REJECTED");
                                }
                        }
                        default -> statuses.clear();
                }
                return statuses;
        }

        public double calculateEstimatedCost(WarrantyClaim wc) {
                List<PartClaim> partClaims = partClaimRepository.findByWarrantyClaimId(wc.getId());
                LocalDate claimDate = wc.getClaimDate().toLocalDate();

                return partClaims.stream()
                                .mapToDouble(p -> {
                                        Set<PartPriceHistory> histories = p.getPart().getPartPriceHistories();
                                        double validPrice = histories.stream()
                                                        .filter(h -> !h.getStartDate().isAfter(claimDate))
                                                        .filter(h -> h.getEndDate() == null
                                                                        || !h.getEndDate().isBefore(claimDate))
                                                        .map(PartPriceHistory::getPrice)
                                                        .findFirst()
                                                        .orElseGet(() -> {
                                                                return histories.stream()
                                                                                .max(Comparator.comparing(
                                                                                                PartPriceHistory::getStartDate))
                                                                                .map(PartPriceHistory::getPrice)
                                                                                .orElse(0.0);
                                                        });

                                        return validPrice * p.getQuantity();
                                })
                                .sum();
        }

        Set<PartClaim> buildPartClaims(Set<PartClaimRequest> requests, WarrantyClaim claim) {
                Set<PartClaim> partClaims = new HashSet<>();
                for (PartClaimRequest pcr : requests) {
                        Part part = partRepository.findById(pcr.getId())
                                        .orElseThrow(() -> new NoSuchElementException(
                                                        "Part not found with id " + pcr.getId()));
                        PartClaim pc = PartClaim.builder()
                                        .quantity(0) // hoặc có thể để null nếu entity cho phép
                                        .part(part)
                                        .warrantyClaim(claim)
                                        .build();
                        partClaims.add(pc);
                }
                return partClaims;
        }

        private List<String> saveAttachmentsToCloudinary(WarrantyClaim wc, MultipartFile[] attachments)
                        throws IOException {
                if (attachments == null || attachments.length == 0)
                        return Collections.emptyList();

                // folder sẽ tự động tạo trong Cloudinary, ví dụ "claims/123"
                String folderName = "claims/" + wc.getId();

                List<String> urls = cloudinaryService.uploadMultiple(attachments, folderName);

                System.out.println(">>> Uploaded to Cloudinary:");
                urls.forEach(System.out::println);

                return urls;
        }

        @Override
        @Transactional
        public CreateClaimResponse handleCreateClaim(CreateClaimRequest request, long serviceCenterId,
                        MultipartFile[] attachments, long userId) throws IOException {

                // Determine claim priority
                WarrantyClaim.ClaimPriority priorityEnum = resolvePriority(request.getPriority());
                WarrantyClaim warrantyClaim = buildWarrantyClaim(request, serviceCenterId, userId, priorityEnum);

                // Assign campaign if recall is agreed
                if (request.isAgreeRecall()) {
                        assignCampaign(warrantyClaim, request.getVin());
                }

                // Check if vehicle already has an active claim (not COMPLETED)
                List<WarrantyClaim> activeClaims = warrantyClaimRepository.findByVehicleVinAndStatusNot(
                                request.getVin(), WarrantyClaim.ClaimStatus.COMPLETED);

                if (!activeClaims.isEmpty()) {
                        WarrantyClaim existing = activeClaims.get(0);
                        throw new RuntimeException("Vehicle " + request.getVin() +
                                        " already has an active claim #" + existing.getId() +
                                        " at service center " + existing.getServiceCenter().getName() +
                                        " with status " + existing.getStatus());
                }

                // Save claim first to get ID
                warrantyClaimRepository.saveAndFlush(warrantyClaim);

                // Assign claim to all defective parts (if any)
                if (request.getDefectivePartIds() != null && !request.getDefectivePartIds().isEmpty()) {

                        List<VehiclePart> vehicleParts = vehiclePartRepository.findActiveByVehicleVinAndPartIds(
                                        request.getVin(), request.getDefectivePartIds());

                        vehicleParts.forEach(vp -> vp.setWarrantyClaim(warrantyClaim));
                        vehiclePartRepository.saveAll(vehicleParts);

                        Set<PartClaimRequest> partRequests = request.getDefectivePartIds().stream()
                                        .map(id -> PartClaimRequest.builder().id(id).build())
                                        .collect(Collectors.toSet());

                        Set<PartClaim> partClaims = buildPartClaims(partRequests, warrantyClaim);
                        partClaimRepository.saveAll(partClaims);
                }

                // Publish event
                applicationEventPublisher.publishEvent(new EntityCreatedEvent<>(this, warrantyClaim));

                // Save attachments
                List<String> attachmentPaths = saveAttachmentsToCloudinary(warrantyClaim, attachments);

                // Send info via WebSocket
                FilterClaimResponse dto = mapToFilterClaimResponse(warrantyClaim, userId);
                messagingTemplate.convertAndSend("/topic/claims", dto);

                return CreateClaimResponse.builder()
                                .success("success")
                                .attachmentBase64(attachmentPaths)
                                .claim(dto)
                                .message("Registered claim successfully")
                                .build();
        }

        private WarrantyClaim.ClaimPriority resolvePriority(String priority) {
                try {
                        return WarrantyClaim.ClaimPriority.valueOf(priority.toUpperCase());
                } catch (IllegalArgumentException | NullPointerException e) {
                        return WarrantyClaim.ClaimPriority.NORMAL;
                }
        }

        private WarrantyClaim buildWarrantyClaim(CreateClaimRequest request, long serviceCenterId, long userId,
                        WarrantyClaim.ClaimPriority priorityEnum) {
                return WarrantyClaim.builder()
                                .description(request.getDescription())
                                .diagnosis(request.getDiagnosis())
                                .mileage(request.getMileage())
                                .vehicle(getVehicleByVin(request.getVin()))
                                .serviceCenter(getServiceCenterById(serviceCenterId))
                                .repairOrder(null)
                                .vehicleParts(new HashSet<>())
                                .serviceCampaign(null)
                                .priority(priorityEnum)
                                .userId(userId)
                                .build();
        }

        private void assignCampaign(WarrantyClaim warrantyClaim, String vin) {
                // Lấy toàn bộ campaign của xe này
                List<CampaignVehicle> campaignVehicles = campaignVehicleRepository.findAllByVehicleVin(vin);
                LocalDate now = LocalDate.now();

                // Lọc ra các campaign đang active (theo thời gian)
                List<CampaignVehicle> activeCampaigns = campaignVehicles.stream()
                                .filter(cv -> {
                                        ServiceCampaign sc = cv.getServiceCampaign();
                                        LocalDate start = sc.getStartDate();
                                        LocalDate end = sc.getEndDate().plusDays(7);
                                        return !now.isBefore(start) && !now.isAfter(end);
                                })
                                .toList();

                // Không có campaign active → bỏ qua
                if (activeCampaigns.isEmpty()) {
                        log.info("Vehicle {} has no active campaign at this time.", vin);
                        return;
                }

                if (activeCampaigns.size() > 1) {
                        String codes = activeCampaigns.stream()
                                        .map(cv -> cv.getServiceCampaign().getCode())
                                        .collect(Collectors.joining(", "));
                        throw new RuntimeException(
                                        "Vehicle " + vin + " belongs to multiple active campaigns: " + codes);
                }

                ServiceCampaign sc = activeCampaigns.get(0).getServiceCampaign();
                warrantyClaim.setServiceCampaign(sc);
                log.info("Assigned campaign {} to claim for VIN {}", sc.getCode(), vin);
        }

        // ================= Change Status =================
        @Override
        @Transactional
        public WarrantyClaimStatusResponse handleChangeStatus(long claimId,
                        WarrantyClaimStatusRequest request, long userId) {

                WarrantyClaim wc = warrantyClaimRepository.findById(claimId)
                                .orElseThrow(() -> new NoSuchElementException("Warranty claim doesn't exist"));

                updateClaimStatus(wc, request.getChangeStatus(), request.getReason(), userId);

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NoSuchElementException("User not found"));

                warrantyClaimRepository.save(wc);
                messagingTemplate.convertAndSend("/topic/claims", mapToFilterClaimResponse(wc, userId));
                applicationEventPublisher.publishEvent(new EntityCreatedEvent<>(this, wc));

                return WarrantyClaimStatusResponse.builder()
                                .repairOrderId(
                                                wc.getRepairOrder() != null ? wc.getRepairOrder().getId() : null)
                                .message("Change Status successfully")
                                .build();

        }

        @Transactional
        private void createVehiclePartAndRepairDetail(WarrantyClaim warrantyClaim, Part part, long quantity) {
                RepairOrder repairOrder = warrantyClaim.getRepairOrder();
                if (repairOrder == null)
                        return;

                Vehicle vehicle = warrantyClaim.getVehicle();
                if (vehicle != null) {
                        // Lấy danh sách tất cả VehiclePart đang active của part này trên xe
                        List<VehiclePart> activeParts = vehiclePartRepository
                                        .findActiveByVehicleVinAndPartId(vehicle.getVin(), part.getId());

                        // Số RepairDetail cần tạo bằng với quantity trong PartClaim
                        for (int i = 0; i < quantity; i++) {
                                // Nếu còn VehiclePart chưa được gắn với claim nào -> gắn vào claim này
                                VehiclePart vehiclePart = activeParts.stream()
                                                .filter(vp -> vp.getWarrantyClaim() == null)
                                                .findFirst()
                                                .orElse(null);

                                if (vehiclePart != null) {
                                        vehiclePart.setWarrantyClaim(warrantyClaim);
                                        vehiclePartRepository.save(vehiclePart);
                                }

                                // Tạo RepairDetail mới cho mỗi part cần sửa
                                RepairDetail repairDetail = RepairDetail.builder()
                                                .repairOrder(repairOrder)
                                                .part(part)
                                                .vehiclePart(vehiclePart) // Có thể null nếu không tìm thấy VehiclePart
                                                .description(part.getName() + (vehiclePart != null
                                                                ? " (Serial: " + vehiclePart.getOldSerialNumber() + ")"
                                                                : ""))
                                                .status(RepairDetail.DetailStatus.PENDING)
                                                .build();
                                repairDetailRepository.save(repairDetail);
                                repairOrder.getRepairDetails().add(repairDetail);
                        }
                }
        }

        private void updateClaimStatus(WarrantyClaim wc, String newStatusStr, String reason, long userId) {
                if (newStatusStr == null || newStatusStr.isBlank())
                        return;
                try {
                        WarrantyClaim.ClaimStatus newStatus = WarrantyClaim.ClaimStatus
                                        .valueOf(newStatusStr.toUpperCase());

                        if (newStatus == WarrantyClaim.ClaimStatus.REJECTED) {
                                User user = userRepository.findById(userId)
                                                .orElseThrow(() -> new NoSuchElementException("User not found"));
                                wc.setRejectBy(user.getRole().toString());
                                wc.setRejectReason(reason);
                                wc.setDecisionDate(LocalDate.now());
                        } else if (newStatus == WarrantyClaim.ClaimStatus.APPROVED) {
                                wc.setDecisionDate(LocalDate.now());
                        }

                        wc.setStatus(newStatus);
                } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Invalid status: " + newStatusStr);
                }
        }

        // ================= Claim Detail =================
        @Override
        public ClaimDetailResponse handleGetClaimDetail(long claimId, Long userId) throws Exception {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NoSuchElementException("User not found"));
                WarrantyClaim wc = warrantyClaimRepository.findById(claimId)
                                .orElseThrow(() -> new NoSuchElementException("Warranty claim doesn't exist"));

                FilterClaimResponse fcr = handleFilterClaim(wc, userId);

                List<DecodeImageReponse> attachments = getClaimAttachmentsFromCloudinary(wc.getId());

                // 🔹 Chuẩn bị builder cho response
                ClaimDetailResponse.ClaimDetailResponseBuilder responseBuilder = ClaimDetailResponse.builder()
                                .fcr(fcr)
                                .images(attachments);

                if ((wc.getStatus() == WarrantyClaim.ClaimStatus.PENDING
                                || wc.getStatus() == WarrantyClaim.ClaimStatus.COMPLETED
                                || wc.getStatus() == WarrantyClaim.ClaimStatus.APPROVED
                                || wc.getStatus() == WarrantyClaim.ClaimStatus.REJECTED)
                                && user.getRole() == User.Role.EVM_STAFF) {
                        List<GetPartClaimResponse> partClaimsAndCampaigns = handleGetPartClaim(claimId, userId);
                        responseBuilder.partClaimsAndCampaigns(partClaimsAndCampaigns);
                }
                // 🔹 Ngược lại → hiển thị partCLiam (danh sách linh kiện + số lượng tồn)
                else {
                        List<PartQuantityResponse> partQuantity = wc.getPartClaims().stream()
                                        .map(pc -> {
                                                Part part = pc.getPart();
                                                long quantity = pc.getQuantity();

                                                PartQuantityResponse.PartQuantityResponseBuilder builder = PartQuantityResponse
                                                                .builder()
                                                                .partId(part.getId())
                                                                .name(part.getName())
                                                                .category(part.getPartCategory())
                                                                .quantity(quantity);

                                                if (user.getRole() == User.Role.EVM_STAFF
                                                                || user.getRole() == User.Role.SC_STAFF
                                                                || user.getRole() == User.Role.ADMIN) {

                                                        // Staff mới cần recommendedQuantity và remainingStock
                                                        RepairManual rm = repairManualRepository
                                                                        .findFirstByPartIdAndModel(part.getId(),
                                                                                        wc.getVehicle().getModel()
                                                                                                        .getName())
                                                                        .orElse(null);
                                                        long recommendedQuantity = rm != null ? rm.getMinQuantity() : 0;

                                                        long remainingStock = part.getPartInventories().stream()
                                                                        .filter(pi -> pi.getServiceCenter() != null
                                                                                        && wc.getServiceCenter() != null
                                                                                        && pi.getServiceCenter().getId()
                                                                                                        .equals(wc.getServiceCenter()
                                                                                                                        .getId()))
                                                                        .mapToLong(PartInventory::getQuantity)
                                                                        .sum();

                                                        builder.recommendedQuantity(recommendedQuantity)
                                                                        .remainingStock(remainingStock);
                                                }

                                                return builder.build();
                                        })
                                        .collect(Collectors.toList());

                        responseBuilder.partCLiam(partQuantity);
                }

                return responseBuilder.build();
        }

        private List<DecodeImageReponse> getClaimAttachmentsFromCloudinary(long claimId) throws Exception {
                Map result = cloudinary.api().resources(ObjectUtils.asMap(
                                "type", "upload",
                                "prefix", "claims/" + claimId,
                                "max_results", 20));
                List<Map<String, Object>> resources = (List<Map<String, Object>>) result.get("resources");

                return resources.stream()
                                .map(r -> DecodeImageReponse.builder()
                                                .image((String) r.get("secure_url"))
                                                .claimAttachmentId(-1L)
                                                .build())
                                .collect(Collectors.toList());
        }

        private List<GetPartClaimResponse> handleGetPartClaim(Long claimId, long userId) {
                // 🔹 Kiểm tra quyền user
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NoSuchElementException("User not found"));
                if (user.getRole() != User.Role.EVM_STAFF) {
                        throw new SecurityException("Access denied: only EVM_STAFF can view part claims.");
                }

                List<PartClaim> partClaims = partClaimRepository.findByWarrantyClaimId(claimId);
                List<GetPartClaimResponse> responses = new ArrayList<>();
                LocalDate today = LocalDate.now();

                for (PartClaim partClaim : partClaims) {
                        Part part = partClaim.getPart();

                        // 🔹 Lọc policy hợp lệ (đang ACTIVE và còn hiệu lực)
                        List<PartPolicy> validPolicies = part.getPartPolicies().stream()
                                        .filter(p -> p.getStatus() == PartPolicy.Status.ACTIVE)
                                        .filter(p -> (p.getStartDate() == null || !p.getStartDate().isAfter(today)))
                                        .filter(p -> (p.getEndDate() == null || !p.getEndDate().isBefore(today)))
                                        .sorted(Comparator.comparing(
                                                        PartPolicy::getEndDate,
                                                        Comparator.nullsLast(Comparator.reverseOrder())))
                                        .collect(Collectors.toList());

                        PartPolicy partPolicy = validPolicies.isEmpty() ? null : validPolicies.get(0);
                        WarrantyPolicy policy = (partPolicy != null) ? partPolicy.getWarrantyPolicy() : null;

                        // 🔹 Lấy giá hiện tại của part
                        Double currentPrice = part.getPartPriceHistories().stream()
                                        .filter(h -> (h.getStartDate() == null || !h.getStartDate().isAfter(today)))
                                        .filter(h -> (h.getEndDate() == null || !h.getEndDate().isBefore(today)))
                                        .sorted(Comparator.comparing(
                                                        PartPriceHistory::getStartDate,
                                                        Comparator.nullsLast(Comparator.reverseOrder())))
                                        .map(PartPriceHistory::getPrice)
                                        .findFirst()
                                        .orElse(0.0);

                        String coverage = (policy != null) ? getCoverageDescription(policy)
                                        : "No warranty coverage information available.";
                        String conditions = (policy != null) ? getConditionDescription(policy)
                                        : "No warranty conditions specified.";

                        // 🔹 Tạo response cho từng PartClaim
                        GetPartClaimResponse response = GetPartClaimResponse.builder()
                                        .partClaimId(partClaim.getId())
                                        .partClaimName(part.getName())
                                        .category(part.getPartCategory())
                                        .quantity(partClaim.getQuantity())
                                        .status(partClaim.getStatus().name())
                                        .estimatedCost(currentPrice)
                                        .policyName(policy != null ? policy.getName() : "N/A")
                                        .description(part.getDescription())
                                        .durationPeriod(policy != null ? policy.getDurationPeriod() : 0)
                                        .effect(partPolicy != null ? partPolicy.getStartDate() : null)
                                        .coverage(coverage)
                                        .conditional(conditions)
                                        .build();

                        responses.add(response);
                }

                return responses;
        }

        private String getCoverageDescription(WarrantyPolicy policy) {
                if (policy.getType() == WarrantyPolicy.PolicyType.PROMOTION) {
                        return "Discounted warranty plan valid for " + policy.getDurationPeriod()
                                        + " months on selected parts.";
                } else {
                        return "Covers standard components such as basic battery pack, single motor, and charger.";
                }
        }

        private String getConditionDescription(WarrantyPolicy policy) {
                if (policy.getType() == WarrantyPolicy.PolicyType.PROMOTION) {
                        return "Warranty void if misuse, modification, or poor maintenance occurs.";
                } else {
                        return "Warranty applies only to manufacturer defects; not valid for damage due to impact or overheating.";
                }
        }

        // ================= Filter Claim =================

        String getVehicleWarrantyPolicyStatus(Vehicle vehicle) {
                // Lấy policy của xe, giả sử Vehicle có liên kết tới WarrantyPolicy thông qua
                // Model/Part
                WarrantyPolicy policy = vehicle.getModel().getModelPolicies().stream()
                                .map(mp -> mp.getWarrantyPolicy())
                                .filter(p -> p.getStatus() == WarrantyPolicy.Status.ACTIVE)
                                .findFirst()
                                .orElse(null);

                if (policy == null) {
                        return "NO_POLICY"; // Xe chưa có chính sách bảo hành
                }

                LocalDate today = LocalDate.now();

                // Tính ngày hết hạn bảo hành theo durationPeriod
                LocalDate warrantyEndDate = vehicle.getPurchaseDate().plusMonths(policy.getDurationPeriod());

                boolean expiredByDate = today.isAfter(warrantyEndDate);
                boolean expiredByMileage = vehicle.getWarrantyClaims().stream()
                                .mapToInt(WarrantyClaim::getMileage)
                                .max()
                                .orElse(0) > policy.getMileageLimit();

                if (expiredByDate || expiredByMileage) {
                        return "EXPIRED"; // Hết hạn bảo hành
                }

                return "ACTIVE"; // Bảo hành còn hiệu lực
        }

        FilterClaimResponse mapToFilterClaimResponse(WarrantyClaim wc, Long userId) {
                Vehicle vehicle = getVehicleByVin(wc.getVehicle().getVin());
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NoSuchElementException("User not found"));
                Customer customer = customerRepository.findById(vehicle.getCustomer().getId())
                                .orElseThrow(() -> new RuntimeException("Customer not found"));
                Model model = modelRepository.findById(vehicle.getModel().getId())
                                .orElseThrow(() -> new RuntimeException("Model not found"));

                CampaignVehicle cv = campaignVehicleRepository.findByVehicleVinAndServiceCampaignId(
                                vehicle.getVin(),
                                wc.getServiceCampaign() != null ? wc.getServiceCampaign().getId() : -1L).orElse(null);

                return FilterClaimResponse.builder()
                                .claimDate(wc.getClaimDate().toLocalDate())
                                .description(wc.getDescription())
                                .price(calculateEstimatedCost(wc))
                                .currentStatus(wc.getStatus().toString())
                                .userName(customer.getName())
                                .userPhoneNumber(customer.getPhoneNumber())
                                .productYear(vehicle.getProductYear())
                                .vin(vehicle.getVin())
                                .licensePlate(vehicle.getLicensePlate())
                                .modelName(model.getName())
                                .modelId(model.getId())
                                .priority(wc.getPriority().toString())
                                .senderName(userRepository.findById(wc.getUserId())
                                                .orElseThrow(() -> new NoSuchElementException("User not found"))
                                                .getName())
                                .id(wc.getId())
                                .serviceCenterName(wc.getServiceCenter().getName())
                                .milege(wc.getMileage())
                                .availableStatuses(handleGetAllStatus(wc, user.getRole()))
                                .rejectReason(wc.getRejectReason())
                                .statusRecall((cv != null && wc.getServiceCampaign() == null) ? "NOT_AGREED_RECALL"
                                                : (cv != null && wc.getServiceCampaign() != null) ? "AGREED_RECALL"
                                                                : "NO_RECALL")
                                .warrantyPolicyStatus(getVehicleWarrantyPolicyStatus(vehicle))
                                .purchaseDate(vehicle.getPurchaseDate())
                                .diagnosis(wc.getDiagnosis())
                                .build();
        }

        List<FilterClaimResponse> handleFilterClaimList(List<WarrantyClaim> wcList, Long userId, Long serviceCenterId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NoSuchElementException("User not found"));

                return wcList.stream()
                                // Chỉ filter theo serviceCenterId nếu > 0
                                .filter(wc -> serviceCenterId == 0
                                                || wc.getServiceCenter().getId().equals(serviceCenterId))
                                .filter(wc -> {
                                        if (user.getRole() == User.Role.EVM_STAFF) {
                                                return wc.getStatus() == WarrantyClaim.ClaimStatus.PENDING
                                                                || wc.getStatus() == WarrantyClaim.ClaimStatus.APPROVED
                                                                || wc.getStatus() == WarrantyClaim.ClaimStatus.COMPLETED
                                                                || (wc.getStatus() == WarrantyClaim.ClaimStatus.REJECTED
                                                                                && wc.getRejectBy().equals(
                                                                                                user.getRole().name()));
                                        }
                                        return true;
                                })
                                .map(wc -> mapToFilterClaimResponse(wc, userId))
                                .sorted(Comparator.comparing(FilterClaimResponse::getId).reversed())
                                .collect(Collectors.toList());
        }

        FilterClaimResponse handleFilterClaim(WarrantyClaim warrantyClaim, Long userId) {
                return mapToFilterClaimResponse(warrantyClaim, userId);
        }

        @Override
        public ClaimDashboardResponse handleClaimDashboard(long serviceCenterId, FilterRequest request, long userId) {

                List<WarrantyClaim> wcList = new ArrayList<>();
                List<FilterClaimResponse> fcrList = new ArrayList<>();
                SummaryClaimResponse scr = new SummaryClaimResponse();

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NoSuchElementException("User not found"));

                List<ServiceCenter> serviceCenters;

                if (serviceCenterId > 0) {
                        // Lấy trung tâm cụ thể
                        ServiceCenter sc = serviceCenterRepository.findById(serviceCenterId)
                                        .orElseThrow(() -> new NoSuchElementException("ServiceCenter not found"));
                        serviceCenters = List.of(sc);
                } else {
                        // Lấy tất cả trung tâm
                        serviceCenters = serviceCenterRepository.findAll();
                }

                // Lấy danh sách claims từ tất cả service center trong list
                for (ServiceCenter sc : serviceCenters) {
                        List<WarrantyClaim> claims = fetchClaimsForDashboard(sc.getId(), user, request, null);
                        wcList.addAll(claims);
                }

                fcrList = handleFilterClaimList(wcList, userId, serviceCenterId);
                scr = handleSummaryClaim(serviceCenterId, wcList, user);

                return ClaimDashboardResponse.builder()
                                .fcr(fcrList)
                                .scr(scr)
                                .build();
        }

        private List<WarrantyClaim> fetchClaimsForDashboard(long serviceCenterId, User user, FilterRequest request,
                        WarrantyClaim.ClaimStatus statusEnum) {
                return warrantyClaimRepository.findByServiceCenterId(serviceCenterId);
        }

        public ClaimSummaryResponse calculateRepeatClaimsRateWithComparison(Long serviceCenterId) {
                LocalDate startOfThisMonth = LocalDate.now().withDayOfMonth(1);
                LocalDate endOfThisMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
                LocalDate startOfLastMonth = startOfThisMonth.minusMonths(1);
                LocalDate endOfLastMonth = startOfThisMonth.minusDays(1);

                long totalThisMonth = warrantyClaimRepository.countByServiceCenterIdAndClaimDateBetween(
                                serviceCenterId, startOfThisMonth.atStartOfDay(),
                                endOfThisMonth.plusDays(1).atStartOfDay());
                long repeatThisMonth = warrantyClaimRepository.countRepeatClaimsInRange(
                                serviceCenterId, startOfThisMonth.atStartOfDay(),
                                endOfThisMonth.plusDays(1).atStartOfDay());
                double rateThisMonth = totalThisMonth == 0 ? 0 : (repeatThisMonth * 100.0) / totalThisMonth;

                long totalLastMonth = warrantyClaimRepository.countByServiceCenterIdAndClaimDateBetween(
                                serviceCenterId, startOfLastMonth.atStartOfDay(),
                                endOfLastMonth.plusDays(1).atStartOfDay());
                long repeatLastMonth = warrantyClaimRepository.countRepeatClaimsInRange(
                                serviceCenterId, startOfLastMonth.atStartOfDay(),
                                endOfLastMonth.plusDays(1).atStartOfDay());
                double rateLastMonth = totalLastMonth == 0 ? 0 : (repeatLastMonth * 100.0) / totalLastMonth;

                double changePercent = rateLastMonth == 0 ? 0 : ((rateThisMonth - rateLastMonth) / rateLastMonth) * 100;

                return ClaimSummaryResponse.builder()
                                .currentRate(rateThisMonth)
                                .changePercent(changePercent)
                                .build();
        }

        public List<ModelFailureResponse> calculateFailureAnalysisByModel(Long serviceCenterId) {
                Long totalClaims = warrantyClaimRepository.countByServiceCenterId(serviceCenterId);
                if (totalClaims == 0) {
                        totalClaims = 1L;
                }

                List<Object[]> modelData = warrantyClaimRepository.countServiceCenterIdAndVehicleModel(serviceCenterId);

                List<ModelFailureResponse> modelFailureList = new ArrayList<>();

                for (Object[] row : modelData) {
                        String model = (String) row[0];
                        Long count = (Long) row[1];
                        double percentage = (count * 100.0) / totalClaims;

                        modelFailureList.add(
                                        ModelFailureResponse.builder()
                                                        .model(model)
                                                        .failureRate(Math.round(percentage * 10.0) / 10.0)
                                                        .build());
                }

                return modelFailureList;
        }

        public List<ClaimsByPriorityResponse> calculateClaimsByPriority(Long serviceCenterId) {
                Long totalClaims = warrantyClaimRepository.countByServiceCenterId(serviceCenterId);
                if (totalClaims == 0)
                        totalClaims = 1L;

                List<Object[]> claimsPriority = warrantyClaimRepository.countServiceCenterAndPriority(serviceCenterId);

                List<ClaimsByPriorityResponse> claimsPriorityList = new ArrayList<>();

                for (Object[] row : claimsPriority) {
                        String priority = row[0].toString(); // NORMAL, HIGH, URGENT
                        Long failures = (Long) row[1];

                        claimsPriorityList.add(
                                        ClaimsByPriorityResponse.builder()
                                                        .priority(priority)
                                                        .failures(failures)
                                                        .build());
                }

                return claimsPriorityList;
        }

        private List<String> getLast6Months() {
                YearMonth now = YearMonth.now();
                List<String> last6Months = new ArrayList<>();
                for (int i = 5; i >= 0; i--) {
                        YearMonth ym = now.minusMonths(i);
                        last6Months.add(ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
                }
                return last6Months;
        }

        private boolean hasCompletedRepairOrder(WarrantyClaim claim) {
                return claim.getRepairOrder() != null
                                && claim.getRepairOrder().getStatus() == RepairOrder.OrderStatus.COMPLETED;
        }

        MonthlyCostSummaryResponse buildMonthlySummary(String monthLabel, long totalClaims, long totalCost,
                        NumberFormat formatter) {
                return MonthlyCostSummaryResponse.builder()
                                .month(monthLabel)
                                .totalClaims(totalClaims)
                                .totalCostFormatted(formatter.format(totalCost))
                                .build();
        }

        long calculateTotalCostForClaims(List<WarrantyClaim> claims) {
                double totalCostDouble = claims.stream()
                                .mapToDouble(this::calculateEstimatedCost) // dùng logic tính cost sẵn có
                                .sum();
                return (long) totalCostDouble;
        }

        List<MonthlyCostSummaryResponse> calculateMonthlySummaries(Long serviceCenterId) {
                List<MonthlyCostSummaryResponse> monthlySummaries = new ArrayList<>();
                NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                List<String> last6Months = getLast6Months();

                for (int i = 5; i >= 0; i--) {
                        YearMonth ym = YearMonth.now().minusMonths(i);
                        int month = ym.getMonthValue();
                        int year = ym.getYear();

                        List<WarrantyClaim> claims = (serviceCenterId == null || serviceCenterId == 0)
                                        ? warrantyClaimRepository.findByMonth(year, month) // <-- Query không lọc theo
                                                                                           // service center
                                        : warrantyClaimRepository.findByServiceCenterAndMonth(serviceCenterId, year,
                                                        month);

                        List<WarrantyClaim> completedClaims = claims.stream()
                                        .filter(c -> c.getStatus() == WarrantyClaim.ClaimStatus.COMPLETED)
                                        .filter(this::hasCompletedRepairOrder)
                                        .toList();

                        long totalClaims = completedClaims.size();
                        long totalCost = calculateTotalCostForClaims(completedClaims);

                        monthlySummaries.add(buildMonthlySummary(
                                        last6Months.get(5 - i),
                                        totalClaims,
                                        totalCost,
                                        formatter));
                }

                return monthlySummaries;
        }

        private List<ComponentCostSummaryResponse> calculateCostByComponent(Long serviceCenterId) {
                NumberFormat formatter = NumberFormat.getInstance(Locale.US);
                List<ComponentCostSummaryResponse> summaries = new ArrayList<>();

                List<Object[]> componentData = (serviceCenterId == null || serviceCenterId == 0)
                                ? partClaimRepository.countFailuresByComponentAllCenters()
                                : partClaimRepository.countFailuresByComponent(serviceCenterId);

                for (Object[] row : componentData) {
                        Part part = (Part) row[0];
                        long totalFailures = (Long) row[1];

                        List<PartClaim> partClaims = (serviceCenterId == null || serviceCenterId == 0)
                                        ? partClaimRepository.findByComponentAllCenters(part.getPartCategory())
                                        : partClaimRepository.findByServiceCenterAndComponent(serviceCenterId,
                                                        part.getPartCategory());

                        partClaims = partClaims.stream()
                                        .filter(pc -> pc.getWarrantyClaim() != null
                                                        && pc.getWarrantyClaim()
                                                                        .getStatus() == WarrantyClaim.ClaimStatus.APPROVED)
                                        .filter(pc -> pc.getWarrantyClaim().getRepairOrder() != null
                                                        && pc.getWarrantyClaim().getRepairOrder()
                                                                        .getStatus() == RepairOrder.OrderStatus.COMPLETED)
                                        .toList();

                        double totalCostDouble = 0;
                        for (PartClaim pc : partClaims) {
                                PartPriceHistory pph = partPriceHistoryRepository
                                                .findCurrentPrice(pc.getPart().getId(),
                                                                pc.getWarrantyClaim().getClaimDate().toLocalDate())
                                                .orElseGet(() -> {
                                                        PartPriceHistory dummy = new PartPriceHistory();
                                                        dummy.setPrice(0.0);
                                                        return dummy;
                                                });

                                totalCostDouble += pph.getPrice() * pc.getQuantity();
                        }

                        long totalCost = (long) totalCostDouble;
                        double avgCost = totalFailures > 0 ? totalCostDouble / totalFailures : 0;

                        summaries.add(ComponentCostSummaryResponse.builder()
                                        .componentName(part.getPartCategory())
                                        .totalFailures(totalFailures)
                                        .totalCostFormatted(formatter.format(totalCost))
                                        .avgCost(avgCost)
                                        .avgCostFormatted(formatter.format(avgCost))
                                        .build());
                }

                return summaries;
        }

        public CostAnalysisResponse handleCalculateClaimCostByMonth(Long serviceCenterId) {

                List<MonthlyCostSummaryResponse> monthlySummaries = calculateMonthlySummaries(serviceCenterId);

                long yMax = monthlySummaries.stream()
                                .mapToLong(ms -> Long.parseLong(ms.getTotalCostFormatted().replace(",", "")))
                                .max()
                                .orElse(0);
                yMax = ((yMax / 10000) + 1) * 10000;

                long totalWarrantyCost = monthlySummaries.stream()
                                .mapToLong(ms -> Long.parseLong(ms.getTotalCostFormatted().replace(",", "")))
                                .sum();

                long totalClaimsProcessed = monthlySummaries.stream()
                                .mapToLong(MonthlyCostSummaryResponse::getTotalClaims)
                                .sum();

                double averageCostPerClaim = totalClaimsProcessed > 0
                                ? (double) totalWarrantyCost / totalClaimsProcessed
                                : 0;

                double totalRevenue = (serviceCenterId == null || serviceCenterId == 0)
                                ? scExpenseReposiotry.findTotalRevenueAllCenters()
                                : scExpenseReposiotry.findTotalRevenueByServiceCenter(serviceCenterId);

                double costOfSalesRatio = totalRevenue > 0
                                ? (totalWarrantyCost / totalRevenue) * 100
                                : 0;

                double averageMonthlyRatio = totalRevenue > 0
                                ? (monthlySummaries.stream()
                                                .mapToDouble(ms -> {
                                                        long totalCost = Long.parseLong(
                                                                        ms.getTotalCostFormatted().replace(",", ""));
                                                        return (totalCost / totalRevenue) * 100;
                                                })
                                                .average()
                                                .orElse(2.5))
                                : 2.5;

                double targetRatio = averageMonthlyRatio * 0.9;

                List<ComponentCostSummaryResponse> componentSummaries = calculateCostByComponent(serviceCenterId);

                return CostAnalysisResponse.builder()
                                .monthlySummaries(monthlySummaries)
                                .componentSummaries(componentSummaries)
                                .yMax(yMax)
                                .totalWarrantyCost(totalWarrantyCost)
                                .averageCostPerClaim(averageCostPerClaim)
                                .totalClaimsProcessed(totalClaimsProcessed)
                                .costOfSalesRatio(costOfSalesRatio)
                                .targetRatio(targetRatio)
                                .build();
        }

        @Override
        public Map<String, Long> getClaimCountsBreakdown(Long serviceCenterId) {
                Map<String, Long> map = new HashMap<>();

                long total, draft, pending, approved, rejected, newToday, newThisWeek;

                LocalDate today = LocalDate.now();
                LocalDateTime startToday = today.atStartOfDay();
                LocalDateTime startTomorrow = startToday.plusDays(1);

                LocalDate startOfWeekDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDateTime startOfWeek = startOfWeekDate.atStartOfDay();
                LocalDateTime startNextWeek = startOfWeek.plusDays(7);

                // 👉 Nếu không có serviceCenterId => EVM Staff xem toàn bộ
                if (serviceCenterId == null || serviceCenterId == 0) {
                        total = warrantyClaimRepository.count();
                        draft = warrantyClaimRepository.countByStatus(WarrantyClaim.ClaimStatus.DRAFT);
                        pending = warrantyClaimRepository.countByStatus(WarrantyClaim.ClaimStatus.PENDING);
                        approved = warrantyClaimRepository.countByStatus(WarrantyClaim.ClaimStatus.APPROVED);
                        rejected = warrantyClaimRepository.countByStatus(WarrantyClaim.ClaimStatus.REJECTED);
                        newToday = warrantyClaimRepository.countByClaimDateBetween(startToday, startTomorrow);
                        newThisWeek = warrantyClaimRepository.countByClaimDateBetween(startOfWeek, startNextWeek);
                } else {
                        // 👉 SC Staff: lọc theo serviceCenterId
                        total = warrantyClaimRepository.countByServiceCenterId(serviceCenterId);
                        draft = warrantyClaimRepository.countByServiceCenterIdAndStatus(serviceCenterId,
                                        WarrantyClaim.ClaimStatus.DRAFT);
                        pending = warrantyClaimRepository.countByServiceCenterIdAndStatus(serviceCenterId,
                                        WarrantyClaim.ClaimStatus.PENDING);
                        approved = warrantyClaimRepository.countByServiceCenterIdAndStatus(serviceCenterId,
                                        WarrantyClaim.ClaimStatus.APPROVED);
                        rejected = warrantyClaimRepository.countByServiceCenterIdAndStatus(serviceCenterId,
                                        WarrantyClaim.ClaimStatus.REJECTED);
                        newToday = warrantyClaimRepository.countByServiceCenterIdAndClaimDateBetween(serviceCenterId,
                                        startToday, startTomorrow);
                        newThisWeek = warrantyClaimRepository.countByServiceCenterIdAndClaimDateBetween(serviceCenterId,
                                        startOfWeek, startNextWeek);
                }

                map.put("total", total);
                map.put("draft", draft);
                map.put("pending", pending);
                map.put("approved", approved);
                map.put("rejected", rejected);
                map.put("newToday", newToday);
                map.put("newThisWeek", newThisWeek);

                return map;
        }

}