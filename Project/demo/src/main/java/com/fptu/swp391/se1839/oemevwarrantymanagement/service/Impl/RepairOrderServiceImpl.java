package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.ChooseTechnicalRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.EmailDetailsRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.FilterRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.RepairOrderVerificationRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ChooseTechnicalResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.DashboardOrderSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.DecodeImageReponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.FilterOrderResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetTechnicalsResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.OrderDashboardResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.OrderDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.OrderSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RepairDetailHistoryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RepairHistoryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RepairOrderVerificationResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.SummaryItemResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.SummaryOrderResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.TechnicalsResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Customer;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Model;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartPriceHistory;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairDetail;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairOrder;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairOrderVerification;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairStep;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.SCExpense;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ServiceCenter;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Vehicle;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.WarrantyClaim;
import com.fptu.swp391.se1839.oemevwarrantymanagement.event.EntityUpdatedEvent;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ModelRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairDetailRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairOrderRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairOrderVerificationRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairStepRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.SCExpenseRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ServiceCenterRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.UserRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehicleRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.WarrantyClaimRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.CloudinaryService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.EmailService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.RepairOrderService;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairOrderServiceImpl implements RepairOrderService {

        final RepairOrderRepository repairOrderRepository;
        final VehicleRepository vehicleRepository;
        final ServiceCenterRepository serviceCenterRepository;
        final WarrantyClaimRepository warrantyClaimRepository;
        final ModelRepository modelRepository;
        final RepairStepRepository repairStepRepository;
        final UserRepository userRepository;
        final RepairDetailRepository repairDetailRepository;
        final SCExpenseRepository scExpenseReposiotry;
        final EmailService emailService;
        final ApplicationEventPublisher applicationEventPublisher;
        final RepairOrderVerificationRepository repairOrderVerificationRepository;
        final CloudinaryService cloudinaryService;
        final Cloudinary cloudinary;

        Vehicle getVehicleByVin(String vin) {
                Vehicle vehicle = this.vehicleRepository.findByVin(vin)
                                .orElseThrow(() -> new NoSuchElementException("Vehicle not found with vin " + vin));
                return vehicle;
        }

        @Override
        public void startRepairOrder(Long repairOrderId) {
                RepairOrder ro = repairOrderRepository.findById(repairOrderId)
                                .orElseThrow(() -> new NoSuchElementException("Repair order not found"));

                // If already started, no-op
                if (ro.getStartDate() != null) {
                        return;
                }

                // Set start date and status
                ro.setStartDate(LocalDateTime.now());
                ro.setStatus(RepairOrder.OrderStatus.IN_PROGRESS);

                // If there are steps, set first waiting step to PENDING
                if (ro.getSteps() != null && !ro.getSteps().isEmpty()) {
                        ro.getSteps().stream()
                                        .sorted((a, b) -> a.getId().compareTo(b.getId()))
                                        .findFirst()
                                        .ifPresent(step -> {
                                                if (step.getStatus() == RepairStep.StepStatus.WAITING) {
                                                        step.setStatus(RepairStep.StepStatus.PENDING);
                                                }
                                        });
                }

                repairOrderRepository.save(ro);
                applicationEventPublisher.publishEvent(new EntityUpdatedEvent<>(this, ro));
        }

        WarrantyClaim getWarrantyClaimId(long id) {
                return this.warrantyClaimRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Warranty claim not found with id " + id));
        }

        ServiceCenter getServiceCenterById(long id) {
                return this.serviceCenterRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Service center not found with id " + id));
        }

        SummaryItemResponse calculateSummary(long current, long previous) {
                long percentage;
                String message;

                if (previous == 0) {
                        percentage = current > 0 ? 100 : 0;
                        message = current > 0 ? "Increase" : "No Change";
                } else if (current > previous) {
                        percentage = (current - previous) * 100 / previous;
                        message = "Increase";
                } else {
                        percentage = (previous - current) * 100 / previous;
                        message = "Decrease";
                }

                return new SummaryItemResponse(percentage, message);
        }

        SummaryItemResponse calculateWeekSummary(Long serviceCenterId) {
                LocalDateTime currentStart = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                LocalDateTime currentEnd = LocalDate.now().with(DayOfWeek.SUNDAY).atTime(LocalTime.MAX);

                long currentWeek;
                long previousWeek;

                if (serviceCenterId != null && serviceCenterId > 0) {
                        currentWeek = repairOrderRepository.countRepairFlWeek(serviceCenterId,
                                        RepairOrder.OrderStatus.COMPLETED, currentStart,
                                        currentEnd);
                        previousWeek = repairOrderRepository.countRepairFlWeek(serviceCenterId,
                                        RepairOrder.OrderStatus.COMPLETED,
                                        currentStart.minusWeeks(1), currentEnd.minusWeeks(1));
                } else {
                        currentWeek = repairOrderRepository.countRepairFlWeekAll(RepairOrder.OrderStatus.COMPLETED,
                                        currentStart, currentEnd);
                        previousWeek = repairOrderRepository.countRepairFlWeekAll(
                                        RepairOrder.OrderStatus.COMPLETED, currentStart.minusWeeks(1),
                                        currentEnd.minusWeeks(1));
                }

                return calculateSummary(currentWeek, previousWeek);
        }

        SummaryItemResponse calculateMonthSummary(Long serviceCenterId) {
                LocalDate today = LocalDate.now();

                LocalDateTime startMonth = today.withDayOfMonth(1).atStartOfDay();
                LocalDateTime endMonth = today.withDayOfMonth(today.lengthOfMonth()).atTime(LocalTime.MAX);

                YearMonth prev = YearMonth.from(today).minusMonths(1);
                LocalDateTime prevStart = prev.atDay(1).atStartOfDay();
                LocalDateTime prevEnd = prev.atEndOfMonth().atTime(LocalTime.MAX);

                long currentMonth;
                long previousMonth;

                if (serviceCenterId != null && serviceCenterId > 0) {
                        currentMonth = repairOrderRepository.countRepairFlStatusAndMonth(
                                        serviceCenterId,
                                        RepairOrder.OrderStatus.COMPLETED,
                                        startMonth, endMonth);

                        previousMonth = repairOrderRepository.countRepairFlStatusAndMonth(
                                        serviceCenterId,
                                        RepairOrder.OrderStatus.COMPLETED,
                                        prevStart, prevEnd);
                } else {
                        currentMonth = repairOrderRepository.countRepairFlStatusAndMonthAll(
                                        RepairOrder.OrderStatus.COMPLETED,
                                        startMonth, endMonth);

                        previousMonth = repairOrderRepository.countRepairFlStatusAndMonthAll(
                                        RepairOrder.OrderStatus.COMPLETED,
                                        prevStart, prevEnd);
                }

                return calculateSummary(currentMonth, previousMonth);
        }

        public long calculateCompleteOneMonth(Long serviceCenterId) {
                LocalDate today = LocalDate.now();
                LocalDateTime startMonth = today.withDayOfMonth(1).atStartOfDay();
                LocalDateTime endMonth = today.withDayOfMonth(today.lengthOfMonth()).atTime(LocalTime.MAX);

                long currentMonth;

                if (serviceCenterId != null && serviceCenterId > 0) {
                        currentMonth = repairOrderRepository.countRepairFlStatusAndMonth(
                                        serviceCenterId,
                                        RepairOrder.OrderStatus.COMPLETED,
                                        startMonth, endMonth);
                } else {
                        currentMonth = repairOrderRepository.countRepairFlStatusAndMonthAll(
                                        RepairOrder.OrderStatus.COMPLETED,
                                        startMonth, endMonth);
                }

                return currentMonth;
        }

        @Override
        public DashboardOrderSummaryResponse findSunSummaryOrder(Long serviceCenterId) {
                boolean status = true;
                long countOrderOneWeek = 0;
                SummaryItemResponse weekResult = new SummaryItemResponse(0, "No Data");
                SummaryItemResponse monthResult = new SummaryItemResponse(0, "No Data");
                long monthComplete = 0;
                try {
                        // Nếu serviceCenterId null hoặc <= 0 → tính tất cả trung tâm
                        if (serviceCenterId != null && serviceCenterId > 0) {
                                countOrderOneWeek = repairOrderRepository.countRepairFlAllStatus(serviceCenterId);
                        } else {
                                countOrderOneWeek = repairOrderRepository.countRepairFlAllStatusAllCenters();
                        }

                        weekResult = calculateWeekSummary(serviceCenterId);
                        monthResult = calculateMonthSummary(serviceCenterId);
                        monthComplete = calculateCompleteOneMonth(serviceCenterId);

                } catch (DataAccessException | PersistenceException e) {
                        log.error("Error while calculating dashboard summary", e);
                        status = false;
                }

                return DashboardOrderSummaryResponse.builder()
                                .countOrderInOneWeek(countOrderOneWeek)
                                .differenceOneWeek(weekResult.getPercentage())
                                .completeOneMonth(monthComplete)
                                .differenceOneMonth(monthResult.getPercentage())
                                .status(status)
                                .build();
        }

        long getOrderByStatus(long serviceCenterId, RepairOrder.OrderStatus status) {
                return repairOrderRepository.countByServiceCenterIdAndStatus(serviceCenterId, status);
        }

        double countAvgComplete(List<RepairOrder> roList) {
                List<RepairOrder> repairOrders = new ArrayList<>();
                for (int i = 0; i < roList.size(); i++) {
                        if (roList.get(i).getStatus() == RepairOrder.OrderStatus.COMPLETED) {
                                repairOrders.add(roList.get(i));
                        }
                }

                long totalDays = 0;
                int count = 0;

                for (RepairOrder ro : repairOrders) {
                        if (ro.getEndDate() != null && ro.getStartDate() != null) {
                                long daysBetween = Duration.between(ro.getStartDate(), ro.getEndDate()).toDays();
                                totalDays += daysBetween;
                                count++;
                        }
                }

                return count > 0 ? (double) totalDays / count : 0;
        }

        int calculateProgress(Long repairOrderId) {
                List<RepairStep> steps = repairStepRepository.findByRepairOrderId(repairOrderId);

                if (steps.isEmpty())
                        return 0;

                long completed = steps.stream()
                                .filter(d -> d.getStatus() == RepairStep.StepStatus.COMPLETED
                                                || d.getStatus() == RepairStep.StepStatus.CANCELLED)
                                .count();

                double progress = (completed * 100.0) / 4; // chia cố định 4

                return (int) Math.round(progress); // làm tròn gần nhất
        }

        SummaryOrderResponse handleSummary(long serviceCenterId, List<RepairOrder> roList) {
                long countInProcess = getOrderByStatus(serviceCenterId, RepairOrder.OrderStatus.IN_PROGRESS);
                long countInWaiting = getOrderByStatus(serviceCenterId, RepairOrder.OrderStatus.WAITING);
                long countInComplete = getOrderByStatus(serviceCenterId, RepairOrder.OrderStatus.COMPLETED);
                double avgDays = countAvgComplete(roList);

                return SummaryOrderResponse.builder()
                                .active(new SummaryItemResponse(countInProcess, "In-process order"))
                                .complete(new SummaryItemResponse(countInComplete, "Complete order"))
                                .waitting(new SummaryItemResponse(countInWaiting, "Waitting order"))
                                .avgComplete(new SummaryItemResponse((long) avgDays, "Avg complete order"))
                                .status(true)
                                .build();
        }

        public List<FilterOrderResponse> handleFilterOrder(List<RepairOrder> roList) {

                List<FilterOrderResponse> forList = new ArrayList<>();

                for (RepairOrder ro : roList) {
                        double progress = calculateProgress(ro.getId());

                        if (progress == 0) {
                                ro.setStatus(RepairOrder.OrderStatus.WAITING);
                        } else if (progress < 100) {
                                ro.setStatus(RepairOrder.OrderStatus.IN_PROGRESS);
                        } else {
                                ro.setStatus(RepairOrder.OrderStatus.COMPLETED);

                                if (ro.getEndDate() == null) {
                                        ro.setEndDate(LocalDateTime.now());
                                }

                                createExpensesIfOrderCompleted(ro);
                        }

                        repairOrderRepository.save(ro);

                        applicationEventPublisher.publishEvent(new EntityUpdatedEvent<>(this, ro));

                        String technicalName = ro.getTechnical() != null ? ro.getTechnical().getName() : "Unknown";

                        WarrantyClaim claim = getWarrantyClaimId(ro.getWarrantyClaim().getId());
                        Vehicle vehicle = getVehicleByVin(claim.getVehicle().getVin());
                        Model model = modelRepository.findById(vehicle.getModel().getId())
                                        .orElseThrow(() -> new RuntimeException("Model not found"));

                        FilterOrderResponse response = FilterOrderResponse.builder()
                                        .repairOrderId(ro.getId())
                                        .claimId(claim.getId())
                                        .claimStatus(claim.getStatus().toString())
                                        .percentInProcess(progress)
                                        .techinal(technicalName)
                                        .prodcutYear(vehicle.getProductYear())
                                        .vin(vehicle.getVin())
                                        .licensePlate(vehicle.getLicensePlate())
                                        .userName(vehicle.getCustomer().getName())
                                        .userPhoneNumber(vehicle.getCustomer().getPhoneNumber())
                                        .modelName(model.getName())
                                        .orderDate(ro.getStartDate()) // sử dụng startDate để sắp xếp
                                        .build();

                        forList.add(response);
                }

                forList.sort(Comparator.comparing(FilterOrderResponse::getRepairOrderId).reversed());

                return forList;
        }

        @Override
        public OrderDashboardResponse handleOrderDashboard(long serviceCenterId, FilterRequest request, Long userId) {
                List<RepairOrder> roList = new ArrayList<>();
                List<FilterOrderResponse> responseList = new ArrayList<>();
                SummaryOrderResponse sor;

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NoSuchElementException("User not found"));
                RepairOrder.OrderStatus statusEnum = null;
                if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                        statusEnum = RepairOrder.OrderStatus.valueOf(request.getStatus().toUpperCase());
                }

                boolean hasSpecificCenter = serviceCenterId > 0;

                // --- Lọc orders theo điều kiện ---
                if (request.getKeyword() == null && request.getStatus() == null) {
                        if (user.getRole() == User.Role.TECHNICIAN) {
                                roList = hasSpecificCenter
                                                ? repairOrderRepository.findByServiceCenterIdAndUserId(serviceCenterId,
                                                                userId)
                                                : repairOrderRepository.findByUserId(userId); // tất cả trung tâm
                        } else {
                                roList = hasSpecificCenter
                                                ? repairOrderRepository.findByServiceCenterId(serviceCenterId)
                                                : repairOrderRepository.findAll(); // tất cả trung tâm
                        }
                } else if (request.getKeyword() == null) { // chỉ lọc status
                        if (user.getRole() == User.Role.TECHNICIAN) {
                                roList = hasSpecificCenter
                                                ? repairOrderRepository.findByServiceCenterIdAndStatusAndUserId(
                                                                serviceCenterId, statusEnum,
                                                                userId)
                                                : repairOrderRepository.findByStatusAndUserId(statusEnum, userId);
                        } else {
                                roList = hasSpecificCenter
                                                ? repairOrderRepository.findByServiceCenterIdAndStatus(serviceCenterId,
                                                                statusEnum)
                                                : repairOrderRepository.findByStatus(statusEnum);
                        }
                } else if (request.getStatus() == null) { // chỉ lọc keyword
                        if (user.getRole() == User.Role.TECHNICIAN) {
                                roList = hasSpecificCenter
                                                ? repairOrderRepository.findByServiceCenterIdAndVehicleVinAndUserId(
                                                                serviceCenterId,
                                                                request.getKeyword(), userId)
                                                : repairOrderRepository.findByVehicleVinAndUserId(request.getKeyword(),
                                                                userId);
                        } else {
                                roList = hasSpecificCenter
                                                ? repairOrderRepository.findByServiceCenterIdAndVehicleVin(
                                                                serviceCenterId,
                                                                request.getKeyword())
                                                : repairOrderRepository.findByVehicleVin(request.getKeyword());
                        }

                        if (roList == null || roList.isEmpty()) {
                                if (user.getRole() == User.Role.TECHNICIAN) {
                                        roList = hasSpecificCenter
                                                        ? repairOrderRepository
                                                                        .findByServiceCenterIdAndCustomerNameAndUserId(
                                                                                        serviceCenterId,
                                                                                        request.getKeyword(),
                                                                                        userId)
                                                        : repairOrderRepository.findByCustomerNameAndUserId(
                                                                        request.getKeyword(), userId);
                                } else {
                                        roList = hasSpecificCenter
                                                        ? repairOrderRepository.findByServiceCenterIdAndCustomerName(
                                                                        serviceCenterId,
                                                                        request.getKeyword())
                                                        : repairOrderRepository
                                                                        .findByCustomerName(request.getKeyword());
                                }
                        }
                } else { // lọc cả keyword + status
                        if (user.getRole() == User.Role.TECHNICIAN) {
                                roList = hasSpecificCenter
                                                ? repairOrderRepository
                                                                .findByServiceCenterIdAndVehicleVinAndStatusAndUserId(
                                                                                serviceCenterId,
                                                                                request.getKeyword(), statusEnum,
                                                                                userId)
                                                : repairOrderRepository.findByVehicleVinAndStatusAndUserId(
                                                                request.getKeyword(), statusEnum,
                                                                userId);
                        } else {
                                roList = hasSpecificCenter
                                                ? repairOrderRepository.findByServiceCenterIdAndVehicleVinAndStatus(
                                                                serviceCenterId,
                                                                request.getKeyword(), statusEnum)
                                                : repairOrderRepository.findByVehicleVinAndStatus(request.getKeyword(),
                                                                statusEnum);
                        }

                        if (roList == null || roList.isEmpty()) {
                                if (user.getRole() == User.Role.TECHNICIAN) {
                                        roList = hasSpecificCenter
                                                        ? repairOrderRepository
                                                                        .findByServiceCenterIdAndCustomerNameAndStatusAndUserId(
                                                                                        serviceCenterId,
                                                                                        request.getKeyword(),
                                                                                        statusEnum, userId)
                                                        : repairOrderRepository.findByCustomerNameAndStatusAndUserId(
                                                                        request.getKeyword(),
                                                                        statusEnum, userId);
                                } else {
                                        roList = hasSpecificCenter
                                                        ? repairOrderRepository
                                                                        .findByServiceCenterIdAndCustomerNameAndStatus(
                                                                                        serviceCenterId,
                                                                                        request.getKeyword(),
                                                                                        statusEnum)
                                                        : repairOrderRepository.findByCustomerNameAndStatus(
                                                                        request.getKeyword(), statusEnum);
                                }
                        }
                }

                // --- Xử lý dữ liệu ---
                responseList = handleFilterOrder(roList);

                // summary order: nếu serviceCenterId = 0 thì tính cho tất cả trung tâm
                long centerIdForSummary = hasSpecificCenter ? serviceCenterId : 0;
                sor = handleSummary(centerIdForSummary, roList);

                return OrderDashboardResponse.builder()
                                .fors(responseList)
                                .sor(sor)
                                .build();
        }

        @Override
        public ChooseTechnicalResponse handleChooseTechnical(long repairOrderId, ChooseTechnicalRequest request) {
                // Lấy RepairOrder theo id
                RepairOrder repairOrder = repairOrderRepository.findById(repairOrderId)
                                .orElseThrow(() -> new NoSuchElementException(
                                                "Repair order not found: " + repairOrderId));

                // Lấy WarrantyClaim liên kết với RepairOrder
                WarrantyClaim claim = repairOrder.getWarrantyClaim();
                if (claim == null) {
                        throw new IllegalStateException("Repair order has no associated warranty claim");
                }

                // ❗ Kiểm tra trạng thái claim
                if (claim.getStatus() != WarrantyClaim.ClaimStatus.APPROVED) {
                        throw new IllegalStateException("Cannot assign technician: warranty claim is not APPROVED");
                }

                // Lấy kỹ thuật viên theo tên
                User technical = userRepository.findByName(request.getTechnicalName());
                if (technical == null) {
                        throw new NoSuchElementException("Technician not found: " + request.getTechnicalName());
                }

                // Gán thông tin kỹ thuật và các trường khác
                repairOrder.setTechnical(technical);
                repairOrder.setStatus(RepairOrder.OrderStatus.PENDING);

                // Lưu RepairOrder
                repairOrderRepository.save(repairOrder);
                applicationEventPublisher.publishEvent(new EntityUpdatedEvent<>(this, repairOrder));

                // Trả về phản hồi
                return ChooseTechnicalResponse.builder()
                                .message("Phân công kỹ thuật viên thành công.")
                                .status(true)
                                .build();
        }

        FilterOrderResponse handleFilterOrder(long orderId) {
                RepairOrder ro = this.repairOrderRepository.findById(orderId)
                                .orElseThrow(() -> new NoSuchElementException("Not find repair order"));

                String technicalName = (ro.getTechnical() != null) ? ro.getTechnical().getName() : null;

                return FilterOrderResponse.builder()
                                .repairOrderId(orderId)
                                .claimId(ro.getWarrantyClaim().getId())
                                .claimStatus(ro.getWarrantyClaim().getStatus().toString())
                                .prodcutYear(ro.getWarrantyClaim().getVehicle().getProductYear())
                                .modelName(ro.getWarrantyClaim().getVehicle().getModel().getName())
                                .vin(ro.getWarrantyClaim().getVehicle().getVin())
                                .licensePlate(ro.getWarrantyClaim().getVehicle().getLicensePlate())
                                .techinal(technicalName)
                                .percentInProcess(calculateProgress(orderId))
                                .build();
        }

        GetTechnicalsResponse handleTechnicalStatus(Long serviceCenterId, long orderId) {
                // Lấy RepairOrder (nếu cần, hiện tại chưa dùng order)
                RepairOrder order = repairOrderRepository.findById(orderId)
                                .orElseThrow(() -> new IllegalArgumentException("Order không tồn tại"));

                // Lấy danh sách kỹ thuật viên có trạng thái AVAILABLE
                List<User> technicians;
                if (serviceCenterId != null && serviceCenterId > 0) {
                        // Lấy theo trung tâm cụ thể
                        technicians = userRepository.findByWorkStatusAndServiceCenterIdAndRole(
                                        User.WorkStatus.AVAILABLE,
                                        serviceCenterId,
                                        User.Role.TECHNICIAN);
                } else {
                        // Lấy tất cả trung tâm
                        technicians = userRepository.findByWorkStatusAndRole(
                                        User.WorkStatus.AVAILABLE,
                                        User.Role.TECHNICIAN);
                }

                List<TechnicalsResponse> result = new ArrayList<>();

                for (User tech : technicians) {
                        // Lấy số lượng công việc đang xử lý của từng kỹ thuật viên
                        long countJobs = repairOrderRepository.countByTechnicalIdAndStatusIn(
                                        tech.getId(),
                                        Arrays.asList(
                                                        RepairOrder.OrderStatus.WAITING,
                                                        RepairOrder.OrderStatus.PENDING,
                                                        RepairOrder.OrderStatus.IN_PROGRESS));

                        result.add(TechnicalsResponse.builder()
                                        .id(tech.getId())
                                        .name(tech.getName())
                                        .countJob(countJobs)
                                        .message("Đang hoạt động")
                                        .build());
                }

                return GetTechnicalsResponse.builder()
                                .technicians(result)
                                .build();
        }

        public OrderDetailResponse handleGetDetailOrder(long serviceCenterId, long orderId) throws Exception {
                List<DecodeImageReponse> attachments = getRepairOrderAttachmentsFromCloudinary(orderId);

                Optional<RepairOrderVerification> verifyOpt = repairOrderVerificationRepository
                                .findByRepairOrderId(orderId);

                RepairOrderVerification verify = verifyOpt.orElse(null);

                return OrderDetailResponse.builder()
                                .filterOrderResponse(handleFilterOrder(orderId))
                                .getTechnicalsResponse(handleTechnicalStatus(serviceCenterId, orderId))
                                .repairOrderId(orderId)
                                .signature(verify != null ? verify.getSignature() : null)
                                .notes(verify != null ? verify.getNotes() : null)
                                .attachmentPaths(attachments)
                                .acceptedResponsibility(verify != null && verify.isAcceptedResponsibility())
                                .verifiedAt(verify != null ? verify.getCreatedAt() : null)
                                .verifiedBy(verify != null
                                                ? userRepository.findById(verify.getCreatedBy())
                                                                .map(u -> u.getName())
                                                                .orElse(null)
                                                : null)
                                .build();
        }

        private List<DecodeImageReponse> getRepairOrderAttachmentsFromCloudinary(long orderId) throws Exception {
                Map<String, Object> result = cloudinary.api().resources(ObjectUtils.asMap(
                                "type", "upload",
                                "prefix", "repair_orders/" + orderId,
                                "max_results", 50));

                List<Map<String, Object>> resources = (List<Map<String, Object>>) result.get("resources");

                if (resources == null || resources.isEmpty()) {
                        return Collections.emptyList();
                }

                return resources.stream()
                                .map(r -> DecodeImageReponse.builder()
                                                .image((String) r.get("secure_url")) // URL trực tiếp từ Cloudinary
                                                .claimAttachmentId(-1L)
                                                .build())
                                .collect(Collectors.toList());
        }

        private void createExpensesIfOrderCompleted(RepairOrder order) {
                if (order.getStatus() != RepairOrder.OrderStatus.COMPLETED)
                        return;

                List<RepairDetail> replacedDetails = repairDetailRepository.findByRepairOrderId(order.getId())
                                .stream()
                                .filter(d -> d.getStatus() == RepairDetail.DetailStatus.REPLACED)
                                .toList();

                for (RepairDetail d : replacedDetails) {
                        Double price = d.getPart().getPartPriceHistories() // Set<PartPriceHistory>
                                        .stream()
                                        .filter(p -> p.getEndDate() == null || p.getEndDate().isAfter(LocalDate.now()))
                                        .max((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate())) // lấy bản mới
                                                                                                         // nhất
                                        .map(PartPriceHistory::getPrice)
                                        .orElse(0.0); // nếu ko có thì default 0

                        SCExpense expense = SCExpense.builder()
                                        .repairOrder(order)
                                        .serviceCenter(order.getWarrantyClaim().getServiceCenter()) // set service
                                                                                                    // center
                                        .description("Chi phí part: " + d.getPart().getName())
                                        .amount(price)
                                        .status(SCExpense.ExpenseStatus.UNPAID) // set status mặc định
                                        .paidDate(null) // chưa thanh toán
                                        .build();
                        scExpenseReposiotry.save(expense);
                }
        }

        public int handleCalculateResponseScore(Long serviceCenterId) {
                boolean hasSpecificCenter = serviceCenterId != null && serviceCenterId > 0;

                // Lấy danh sách orders theo trung tâm hoặc tất cả
                List<RepairOrder> allOrders = hasSpecificCenter
                                ? repairOrderRepository.findByServiceCenterId(serviceCenterId)
                                : repairOrderRepository.findAll();

                double avgResponseHours = allOrders.stream()
                                .filter(o -> o.getWarrantyClaim() != null && o.getStartDate() != null)
                                .mapToDouble(o -> Duration
                                                .between(o.getWarrantyClaim().getClaimDate(), o.getStartDate())
                                                .toHours())
                                .average()
                                .orElse(0);

                // Công thức tính điểm: càng nhanh, điểm càng cao
                return (int) Math.round(Math.max(0, 100 - avgResponseHours * 5));
        }

        public int handleCalculatePerformanceMetrics(Long serviceCenterId) {
                boolean hasSpecificCenter = serviceCenterId != null && serviceCenterId > 0;

                // Lấy danh sách completed orders
                List<RepairOrder> completedOrders = hasSpecificCenter
                                ? repairOrderRepository.findByServiceCenterIdAndStatus(serviceCenterId,
                                                RepairOrder.OrderStatus.COMPLETED)
                                : repairOrderRepository.findByStatus(RepairOrder.OrderStatus.COMPLETED); // method tổng
                                                                                                         // cho tất cả
                                                                                                         // trung
                                                                                                         // tâm

                long onTimeCount = completedOrders.stream()
                                .filter(o -> o.getStartDate() != null && o.getEndDate() != null)
                                .filter(o -> Duration.between(o.getStartDate(), o.getEndDate()).toDays() <= 3) // thời
                                                                                                               // hạn 3
                                                                                                               // ngày
                                .count();

                return completedOrders.isEmpty() ? 0
                                : (int) Math.round((onTimeCount * 100.0) / completedOrders.size());
        }

        public int handleCalculateOverdueRepairs(Long serviceCenterId) {
                LocalDateTime now = LocalDateTime.now();
                boolean hasSpecificCenter = serviceCenterId != null && serviceCenterId > 0;

                // Lấy danh sách orders theo trung tâm hoặc tất cả
                List<RepairOrder> orders = hasSpecificCenter
                                ? repairOrderRepository.findByServiceCenterId(serviceCenterId)
                                : repairOrderRepository.findAll();

                long count = orders.stream()
                                // chỉ tính orders có startDate và endTime
                                .filter(o -> o.getStartDate() != null && o.getEndTime() > 0)
                                .filter(o -> o.getEndDate() == null)
                                .peek(order -> {
                                        WarrantyClaim claim = order.getWarrantyClaim();
                                        if (claim != null) {
                                                long daysSinceClaim = Duration.between(claim.getClaimDate(), now)
                                                                .toDays();

                                                if (daysSinceClaim > 7) {
                                                        claim.setPriority(WarrantyClaim.ClaimPriority.HIGH);
                                                } else {
                                                        claim.setPriority(WarrantyClaim.ClaimPriority.NORMAL);
                                                }

                                                warrantyClaimRepository.save(claim);
                                        }
                                })
                                // Lọc các order quá hạn: tổng estimatedHours của steps < endTime của order
                                .filter(o -> {
                                        List<RepairStep> steps = repairStepRepository.findByRepairOrderId(o.getId());
                                        double totalEstimatedHours = steps.stream()
                                                        .filter(s -> s.getEstimatedHours() != null)
                                                        .mapToDouble(RepairStep::getEstimatedHours)
                                                        .sum();

                                        // endTime đang là int (giả sử đơn vị là giờ)
                                        return totalEstimatedHours > o.getEndTime();
                                })
                                .count();

                return (int) count;
        }

        public int hanldeCalculateCompleteToday(long serviceCenterId) {
                LocalDate today = LocalDate.now();
                LocalDateTime startToday = today.atStartOfDay();
                LocalDateTime startTomorrow = today.plusDays(1).atStartOfDay();

                boolean hasSpecificCenter = serviceCenterId > 0;

                long count = hasSpecificCenter
                                ? repairOrderRepository.countByServiceCenterIdAndStatusAndEndDateBetween(
                                                serviceCenterId,
                                                RepairOrder.OrderStatus.COMPLETED,
                                                startToday,
                                                startTomorrow)
                                : repairOrderRepository.countByStatusAndEndDateBetween(
                                                RepairOrder.OrderStatus.COMPLETED,
                                                startToday,
                                                startTomorrow); // method tổng cho tất cả trung tâm

                return (int) count;
        }

        public double handleCalculateAvgDays(long serviceCenterId) {
                boolean hasSpecificCenter = serviceCenterId > 0;

                // Lấy danh sách orders hoàn thành theo trung tâm hoặc tất cả
                List<RepairOrder> completedOrders = hasSpecificCenter
                                ? repairOrderRepository.findByServiceCenterIdAndStatus(serviceCenterId,
                                                RepairOrder.OrderStatus.COMPLETED)
                                : repairOrderRepository.findByStatus(RepairOrder.OrderStatus.COMPLETED); // method tổng
                                                                                                         // cho tất cả
                                                                                                         // trung
                                                                                                         // tâm

                double avgDays = completedOrders.stream()
                                .filter(o -> o.getStartDate() != null && o.getEndDate() != null)
                                .mapToDouble(o -> Duration.between(o.getStartDate(), o.getEndDate()).toHours())
                                .average()
                                .orElse(0);

                return new BigDecimal(avgDays)
                                .setScale(2, RoundingMode.HALF_UP)
                                .doubleValue();
        }

        public int handleCalculateResolutionRate(Long serviceCenterId) {
                boolean hasSpecificCenter = serviceCenterId != null && serviceCenterId > 0;

                long totalOrders = hasSpecificCenter
                                ? repairOrderRepository.countByServiceCenterId(serviceCenterId)
                                : repairOrderRepository.countAllOrders();

                long completedOrders = hasSpecificCenter
                                ? repairOrderRepository.countByServiceCenterIdAndStatus(serviceCenterId,
                                                RepairOrder.OrderStatus.COMPLETED)
                                : repairOrderRepository.countAllOrdersByStatus(RepairOrder.OrderStatus.COMPLETED);

                return totalOrders == 0 ? 0 : (int) Math.round((completedOrders * 100.0) / totalOrders);
        }

        public OrderSummaryResponse handleCalculateResolutionRateDifferent(Long serviceCenterId) {
                boolean hasSpecificCenter = serviceCenterId != null && serviceCenterId > 0;

                LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
                LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

                LocalDate startOfPrevMonth = startOfMonth.minusMonths(1);
                LocalDate endOfPrevMonth = startOfMonth.minusDays(1);

                long totalCurrent = hasSpecificCenter
                                ? repairOrderRepository.countByServiceCenterIdAndStartDateBetween(
                                                serviceCenterId, startOfMonth.atStartOfDay(),
                                                endOfMonth.plusDays(1).atStartOfDay())
                                : repairOrderRepository.countAllOrdersBetween(startOfMonth.atStartOfDay(),
                                                endOfMonth.plusDays(1).atStartOfDay());

                long completedCurrent = hasSpecificCenter
                                ? repairOrderRepository.countByServiceCenterIdAndStatusAndStartDateBetween(
                                                serviceCenterId, RepairOrder.OrderStatus.COMPLETED,
                                                startOfMonth.atStartOfDay(), endOfMonth.plusDays(1).atStartOfDay())
                                : repairOrderRepository.countAllOrdersByStatusBetween(
                                                RepairOrder.OrderStatus.COMPLETED,
                                                startOfMonth.atStartOfDay(), endOfMonth.plusDays(1).atStartOfDay());

                double currentRate = totalCurrent == 0 ? 0 : (completedCurrent * 100.0) / totalCurrent;

                long totalPrev = hasSpecificCenter
                                ? repairOrderRepository.countByServiceCenterIdAndStartDateBetween(
                                                serviceCenterId, startOfPrevMonth.atStartOfDay(),
                                                endOfPrevMonth.plusDays(1).atStartOfDay())
                                : repairOrderRepository.countAllOrdersBetween(startOfPrevMonth.atStartOfDay(),
                                                endOfPrevMonth.plusDays(1).atStartOfDay());

                long completedPrev = hasSpecificCenter
                                ? repairOrderRepository.countByServiceCenterIdAndStatusAndStartDateBetween(
                                                serviceCenterId, RepairOrder.OrderStatus.COMPLETED,
                                                startOfPrevMonth.atStartOfDay(),
                                                endOfPrevMonth.plusDays(1).atStartOfDay())
                                : repairOrderRepository.countAllOrdersByStatusBetween(
                                                RepairOrder.OrderStatus.COMPLETED,
                                                startOfPrevMonth.atStartOfDay(),
                                                endOfPrevMonth.plusDays(1).atStartOfDay());

                double prevRate = totalPrev == 0 ? 0 : (completedPrev * 100.0) / totalPrev;

                double difference = (prevRate == 0) ? (currentRate > 0 ? 100 : 0)
                                : ((currentRate - prevRate) / prevRate) * 100;

                return OrderSummaryResponse.builder()
                                .averageResolution(
                                                new SummaryItemResponse((long) currentRate, "Average Resolution Time"))
                                .diffirence(new SummaryItemResponse(
                                                (long) Math.round(difference),
                                                difference >= 0 ? "Increase" : "Decrease"))
                                .build();
        }

        @Override
        public String sendRepairCompletedEmail(Long repairOrderId) {
                RepairOrder repairOrder = repairOrderRepository.findById(repairOrderId)
                                .orElseThrow(() -> new RuntimeException("Repair order not found"));

                if (repairOrder.getStatus() != RepairOrder.OrderStatus.COMPLETED) {
                        throw new RuntimeException("Repair order is not completed yet");
                }
                // Join through relationships:
                WarrantyClaim claim = repairOrder.getWarrantyClaim();
                if (claim == null || claim.getVehicle() == null || claim.getVehicle().getCustomer() == null) {
                        throw new RuntimeException("Customer information not found for this repair order");
                }

                Customer customer = claim.getVehicle().getCustomer();
                String customerName = customer.getName();
                String customerEmail = customer.getEmail();
                String vin = claim.getVehicle().getVin();

                // Create email content
                String subject = "Repair Completion Notification - OEM EV Warranty";
                String htmlContent = "<html><body>"
                                + "<h3>Dear " + customerName + ",</h3>"
                                + "<p>Your Vinfast" + claim.getVehicle().getModel().getName() + " (VIN: <b>" + vin
                                + "</b>) has been successfully repaired.</p>"
                                + "<p>Please visit our service center to pick up your vehicle.</p>"
                                + "<p>If you have any questions, feel free to contact us.</p>"
                                + "<p>Service center opens 7:00AM - 18:00PM from Monday to Tuesday</p>"
                                + "<p>Thank you for trusting OEM EV Warranty service!</p>"
                                + "<br><b>OEM EV Warranty Team</b>"
                                + "</body></html>";

                // Build email request
                EmailDetailsRequest emailDetails = new EmailDetailsRequest();
                emailDetails.setRecipient(customerEmail);
                emailDetails.setSubject(subject);
                emailDetails.setMessageBody(htmlContent);

                // Send email
                emailService.sendHtmlMail(emailDetails);
                applicationEventPublisher.publishEvent(new EntityUpdatedEvent<>(this, repairOrder));
                return "Repair completion email sent to " + customerEmail;
        }

        @Transactional
        public RepairOrderVerificationResponse verifyRepairOrder(long repairOrderId,
                        RepairOrderVerificationRequest request,
                        long userId, MultipartFile[] attachments) throws IOException {

                RepairOrder ro = repairOrderRepository.findById(repairOrderId)
                                .orElseThrow(() -> new NoSuchElementException("Repair order not found"));

                WarrantyClaim wc = warrantyClaimRepository.findById(ro.getWarrantyClaim().getId())
                                .orElseThrow(() -> new NoSuchElementException("Claim not found"));

                if (!request.isAcceptedResponsibility()) {
                        throw new RuntimeException("You must accept responsibility to complete verification.");
                }

                RepairOrderVerification verification = new RepairOrderVerification();
                verification.setRepairOrder(ro);
                verification.setSignature(request.getSignature());
                verification.setNotes(request.getNotes());
                verification.setAcceptedResponsibility(request.isAcceptedResponsibility());
                verification.setCreatedAt(LocalDateTime.now());
                verification.setCreatedBy(userId);

                List<String> attachmentPaths = saveVerificationAttachmentsToCloudinary(ro.getId(), attachments);

                repairOrderVerificationRepository.save(verification);

                // Cập nhật repair order completed
                ro.setSupervisorApproved(true);
                ro.setStatus(RepairOrder.OrderStatus.COMPLETED);
                repairOrderRepository.save(ro);

                wc.setStatus(WarrantyClaim.ClaimStatus.COMPLETED);
                warrantyClaimRepository.save(wc);

                sendRepairOrderCompletedEmail(wc.getVehicle().getCustomer().getName(),
                                wc.getVehicle().getCustomer().getEmail(), ro);

                // Response
                User tech = userRepository.findById(userId).orElseThrow();
                RepairOrderVerificationResponse response = new RepairOrderVerificationResponse();
                response.setRepairOrderId(ro.getId());
                response.setSignature(verification.getSignature());
                response.setNotes(verification.getNotes());
                response.setAttachmentPaths(attachmentPaths);
                response.setAcceptedResponsibility(verification.isAcceptedResponsibility());
                response.setVerifiedAt(verification.getCreatedAt());
                response.setVerifiedBy(tech.getName());

                return response;
        }

        private List<String> saveVerificationAttachmentsToCloudinary(Long repairOrderId, MultipartFile[] attachments)
                        throws IOException {
                if (attachments == null || attachments.length == 0)
                        return Collections.emptyList();

                // Cloudinary sẽ tự tạo folder "repair_orders/{id}"
                String folderName = "repair_orders/" + repairOrderId;

                List<String> urls = cloudinaryService.uploadMultiple(attachments, folderName);

                System.out.println(">>> Uploaded to Cloudinary:");
                urls.forEach(System.out::println);

                return urls;
        }

        void sendRepairOrderCompletedEmail(String ownerName, String ownerEmail, RepairOrder repairOrder) {

                String subject = "Your Repair Order Has Been Completed";

                String htmlBody = String.format(
                                "<html>" +
                                                "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>" +
                                                "<p>Dear %s,</p>" +
                                                "<p>We are pleased to inform you that your repair order with ID <strong>%s</strong> has been completed successfully.</p>"
                                                +
                                                "<p>You can find the details below:</p>" +
                                                "<hr/>" +
                                                "<p><strong>Repair Order ID:</strong> %s</p>" +
                                                "<p><strong>Vehicle VIN:</strong> %s</p>" +
                                                "<p><strong>Status:</strong> %s</p>" +
                                                "<hr/>" +
                                                "<p>Thank you for trusting our service center. If you have any questions, please contact us.</p>"
                                                +
                                                "<p>Best regards,<br/>OEM EV Warranty Service Team</p>" +
                                                "</body>" +
                                                "</html>",
                                ownerName,
                                repairOrder.getId(),
                                repairOrder.getId(),
                                repairOrder.getWarrantyClaim().getVehicle().getVin(),
                                repairOrder.getStatus().toString());

                EmailDetailsRequest details = new EmailDetailsRequest();
                details.setRecipient(ownerEmail);
                details.setSubject(subject);
                details.setMessageBody(htmlBody);

                emailService.sendHtmlMail(details);
        }

        @Override
        public List<RepairHistoryResponse> getRecentRepairHistoryByVin(String vin) {
                List<RepairOrder> orders = repairOrderRepository
                                .findRecentRepairOrdersByVin(vin, PageRequest.of(0, 4));

                return orders.stream()
                                .map(order -> {
                                        WarrantyClaim claim = order.getWarrantyClaim();

                                        // Map RepairDetail → RepairDetailHistoryResponse
                                        List<RepairDetailHistoryResponse> detailResponses = order.getRepairDetails()
                                                        .stream()
                                                        .map(detail -> RepairDetailHistoryResponse.builder()
                                                                        .partName(detail.getPart() != null
                                                                                        ? detail.getPart().getName()
                                                                                        : "Unknown part")
                                                                        .status(detail.getStatus().name())
                                                                        .description(detail.getDescription())
                                                                        .build())
                                                        .collect(Collectors.toList());

                                        return RepairHistoryResponse.builder()
                                                        .orderId(order.getId())
                                                        .status(order.getStatus().toString())
                                                        .startDate(order.getStartDate())
                                                        .endDate(order.getEndDate())
                                                        .claimDescription(claim != null ? claim.getDescription() : null)
                                                        .claimMileage(claim != null ? claim.getMileage() : 0)
                                                        .details(detailResponses)
                                                        .build();
                                })
                                .collect(Collectors.toList());
        }
}