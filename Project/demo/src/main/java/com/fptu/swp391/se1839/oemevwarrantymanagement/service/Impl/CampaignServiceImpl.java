package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CreateCampaignRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.EmailDetailsRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.UpdateCampaignRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.FilterClaimResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllVehicleCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ServiceCampaignDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ServiceCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ServiceCampaignSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.VehicleCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.VehicleInCampaignResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.CampaignVehicle;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.CampaignVehicle.CampaignVehicleStatus;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Customer;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ServiceCampaign;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Vehicle;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.CampaignVehicleRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ServiceCampaignRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.VehicleRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.CampaignService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.EmailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CampaignServiceImpl implements CampaignService {

        private final ServiceCampaignRepository serviceCampaignRepository;
        private final VehicleRepository vehicleRepository;
        private final CampaignVehicleRepository campaignVehicleRepository;
        private final EmailService emailService;

        @Override
        public ServiceCampaignResponse handleCreateCampaign(CreateCampaignRequest request) {

                if (serviceCampaignRepository.existsByCode(request.getCode())) {
                        throw new IllegalArgumentException("Campaign code already exists");
                }

                if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
                        throw new IllegalArgumentException("End date must be after start date");
                }

                if (request.getProduceDateFrom().isAfter(request.getProduceDateTo())) {
                        throw new IllegalArgumentException("Produce date range is invalid");
                }

                ServiceCampaign campaign = ServiceCampaign.builder()
                                .name(request.getName())
                                .description(request.getDescription())
                                .startDate(request.getStartDate())
                                .endDate(request.getEndDate())
                                .produceDateFrom(request.getProduceDateFrom())
                                .produceDateTo(request.getProduceDateTo())
                                .code(request.getCode())
                                .build();

                serviceCampaignRepository.save(campaign);

                List<Vehicle> vehicles = vehicleRepository.findByProductionDateBetween(
                                request.getProduceDateFrom(),
                                request.getProduceDateTo());

                List<CampaignVehicle> campaignVehicles = vehicles.stream()
                                .filter(v -> !campaignVehicleRepository
                                                .existsByServiceCampaignIdAndVehicleVin(campaign.getId(), v.getVin()))
                                .map(vehicle -> CampaignVehicle.builder()
                                                .serviceCampaign(campaign)
                                                .vehicle(vehicle)
                                                .status(CampaignVehicleStatus.NOTIFIED)
                                                .build())
                                .toList();

                campaignVehicleRepository.saveAll(campaignVehicles);

                return ServiceCampaignResponse.builder()
                                .id(campaign.getId())
                                .name(campaign.getName())
                                .description(campaign.getDescription())
                                .startDate(campaign.getStartDate())
                                .endDate(campaign.getEndDate())
                                .produceDateFrom(campaign.getProduceDateFrom())
                                .produceDateTo(campaign.getProduceDateTo())
                                .code(campaign.getCode())
                                .totalVehicles(campaignVehicles.size())
                                .build();
        }

        @Override
        public GetAllCampaignResponse handleGetAllCampaigns() {
                List<ServiceCampaignResponse> list = serviceCampaignRepository.findAll().stream()
                                .map(c -> ServiceCampaignResponse.builder()
                                                .id(c.getId())
                                                .name(c.getName())
                                                .description(c.getDescription())
                                                .startDate(c.getStartDate())
                                                .endDate(c.getEndDate())
                                                .produceDateFrom(c.getProduceDateFrom())
                                                .produceDateTo(c.getProduceDateTo())
                                                .code(c.getCode())
                                                .totalVehicles(c.getCampaignVehicles().size())
                                                .build())
                                .sorted(Comparator.comparing(ServiceCampaignResponse::getId).reversed())
                                .toList();

                return GetAllCampaignResponse.builder().campaigns(list).build();
        }

        @Override
        public ServiceCampaignDetailResponse handleGetCampaignById(Long id) {
                ServiceCampaign campaign = serviceCampaignRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));

                List<String> vins = campaign.getCampaignVehicles().stream()
                                .map(cv -> cv.getVehicle().getVin())
                                .toList();

                return ServiceCampaignDetailResponse.builder()
                                .id(campaign.getId())
                                .name(campaign.getName())
                                .description(campaign.getDescription())
                                .startDate(campaign.getStartDate())
                                .endDate(campaign.getEndDate())
                                .produceDateFrom(campaign.getProduceDateFrom())
                                .produceDateTo(campaign.getProduceDateTo())
                                .code(campaign.getCode())
                                .vehicleVins(vins)
                                .build();
        }

        @Override
        public void handleDeleteCampaign(Long id) {
                ServiceCampaign campaign = serviceCampaignRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
                if (!campaign.getCampaignVehicles().isEmpty()) {
                        throw new IllegalArgumentException("Cannot delete campaign that has assigned vehicles.");
                }
                if (campaign.getStartDate().isBefore(LocalDate.now())) {
                        throw new IllegalArgumentException("Cannot delete campaign that has already started.");
                }
                serviceCampaignRepository.deleteById(id);
        }

        @Override
        public ServiceCampaignResponse handleUpdateCampaign(UpdateCampaignRequest request, Long id) {
                ServiceCampaign campaign = serviceCampaignRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
                // ===== Validate logic =====
                if (request.getEndDate() != null && request.getStartDate() != null
                                && request.getEndDate().isBefore(request.getStartDate())) {
                        throw new IllegalArgumentException("End date must be after start date");
                }

                if (request.getProduceDateFrom() != null && request.getProduceDateTo() != null
                                && request.getProduceDateFrom().isAfter(request.getProduceDateTo())) {
                        throw new IllegalArgumentException("Produce date range is invalid");
                }

                // ===== Cập nhật field =====
                if (request.getName() != null)
                        campaign.setName(request.getName());
                if (request.getDescription() != null)
                        campaign.setDescription(request.getDescription());
                if (request.getStartDate() != null)
                        campaign.setStartDate(request.getStartDate());
                if (request.getEndDate() != null)
                        campaign.setEndDate(request.getEndDate());
                if (request.getProduceDateFrom() != null)
                        campaign.setProduceDateFrom(request.getProduceDateFrom());
                if (request.getProduceDateTo() != null)
                        campaign.setProduceDateTo(request.getProduceDateTo());
                // ===== Lấy danh sách CampaignVehicle cũ =====
                List<CampaignVehicle> oldCampaignVehicles = campaignVehicleRepository
                                .findByServiceCampaignId(campaign.getId());

                // Lưu lại VIN của các xe đã COMPLETED
                Set<String> completedVins = oldCampaignVehicles.stream()
                                .filter(cv -> cv.getStatus() == CampaignVehicle.CampaignVehicleStatus.COMPLETED)
                                .map(cv -> cv.getVehicle().getVin())
                                .collect(Collectors.toSet());

                // ===== Lấy danh sách xe mới nằm trong khoảng ngày sản xuất =====
                List<Vehicle> newVehicles = vehicleRepository.findByProductionDateBetween(
                                campaign.getProduceDateFrom(),
                                campaign.getProduceDateTo());

                Set<String> newVins = newVehicles.stream()
                                .map(Vehicle::getVin)
                                .collect(Collectors.toSet());

                // Giữ lại VIN completed để không bị xóa
                newVins.addAll(completedVins);

                // ===== Xóa các xe không còn hợp lệ và chưa completed =====
                List<CampaignVehicle> toDelete = oldCampaignVehicles.stream()
                                .filter(cv -> !newVins.contains(cv.getVehicle().getVin())
                                                && cv.getStatus() != CampaignVehicle.CampaignVehicleStatus.COMPLETED)
                                .toList();

                if (!toDelete.isEmpty()) {
                        campaignVehicleRepository.deleteAllInBatch(toDelete);
                }

                // ===== Thêm xe mới chưa có trong campaign =====
                Set<String> existingVins = oldCampaignVehicles.stream()
                                .map(cv -> cv.getVehicle().getVin())
                                .collect(Collectors.toSet());

                List<CampaignVehicle> vehiclesToAdd = newVehicles.stream()
                                .filter(v -> !existingVins.contains(v.getVin()))
                                .map(v -> CampaignVehicle.builder()
                                                .serviceCampaign(campaign)
                                                .vehicle(v)
                                                .status(CampaignVehicle.CampaignVehicleStatus.NOTIFIED)
                                                .build())
                                .toList();

                if (!vehiclesToAdd.isEmpty()) {
                        campaignVehicleRepository.saveAll(vehiclesToAdd);
                }

                // ===== Lưu lại campaign =====
                serviceCampaignRepository.save(campaign);
                long totalVehicles = 0;
                return ServiceCampaignResponse.builder()
                                .id(campaign.getId())
                                .name(campaign.getName())
                                .description(campaign.getDescription())
                                .startDate(campaign.getStartDate())
                                .endDate(campaign.getEndDate())
                                .produceDateFrom(campaign.getProduceDateFrom())
                                .produceDateTo(campaign.getProduceDateTo())
                                .code(campaign.getCode())
                                .totalVehicles((int) totalVehicles)
                                .build();
        }

        @Override
        public List<ServiceCampaignSummaryResponse> handleGetCampaignByVin(String vin) {

                List<CampaignVehicle> campaignVehicles = campaignVehicleRepository.findAllByVehicleVin(vin);

                if (campaignVehicles.isEmpty()) {
                        throw new IllegalArgumentException("No campaign found for VIN: " + vin);
                }

                return campaignVehicles.stream()
                                .filter(cv -> cv.getStatus() != CampaignVehicle.CampaignVehicleStatus.COMPLETED)
                                .map(cv -> {
                                        ServiceCampaign c = cv.getServiceCampaign();
                                        return ServiceCampaignSummaryResponse.builder()
                                                        .campaignId(c.getId())
                                                        .campaignCode(c.getCode())
                                                        .campaignName(c.getName())
                                                        .description(c.getDescription())
                                                        .startDate(c.getStartDate())
                                                        .endDate(c.getEndDate())
                                                        .produceDateFrom(c.getProduceDateFrom())
                                                        .produceDateTo(c.getProduceDateTo())
                                                        .status(cv.getStatus().name())
                                                        .build();
                                })
                                .toList();
        }

        @Override
        public GetAllVehicleCampaignResponse handleGetAllVehiclesWithCampaigns() {
                List<VehicleCampaignResponse> list = campaignVehicleRepository.findAll().stream()
                                .map(cv -> {
                                        Vehicle v = cv.getVehicle();
                                        return VehicleCampaignResponse.builder()
                                                        .vin(v.getVin())
                                                        .model(v.getModel().getName()) // Access the name of the Model
                                                                                       // object
                                                        .customerName(v.getCustomer() != null
                                                                        ? v.getCustomer().getName()
                                                                        : "") // Assuming Customer has a getName()
                                                                              // method
                                                        .customerEmail(v.getCustomer() != null
                                                                        ? v.getCustomer().getEmail()
                                                                        : "")
                                                        .customerPhone(v.getCustomer() != null
                                                                        ? v.getCustomer().getPhoneNumber()
                                                                        : "")
                                                        .build();
                                })
                                .toList();

                return GetAllVehicleCampaignResponse.builder()
                                .vehicles(list)
                                .build();
        }

        @Override
        public String notifyCustomersByCampaign(Long campaignId) {
                ServiceCampaign campaign = serviceCampaignRepository.findById(campaignId)
                                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));

                // Lấy tất cả xe trong campaign
                List<CampaignVehicle> campaignVehicles = campaignVehicleRepository.findByServiceCampaignId(campaignId);

                // Thu thập email khách hàng hợp lệ (tránh trùng)
                List<String> customerEmails = campaignVehicles.stream()
                                .map(cv -> cv.getVehicle().getCustomer())
                                .filter(Objects::nonNull)
                                .map(Customer::getEmail)
                                .filter(Objects::nonNull)
                                .distinct()
                                .toList();

                if (customerEmails.isEmpty()) {
                        return "No valid customer emails found for campaign: " + campaign.getName();
                }

                // Chủ đề và nội dung email chuyên nghiệp
                String subject = "Important Notice: Vehicle Recall Campaign - " + campaign.getName();

                String message = String.format(
                                """
                                                <div style="font-family: Arial, sans-serif; color: #333;">
                                                    <h2 style="color: #004aad;">Vehicle Recall Notification</h2>
                                                    <p>Dear Valued Customer,</p>
                                                    <p>
                                                        We would like to inform you that your vehicle has been identified as part of our
                                                        <b>recall campaign "%s"</b>.
                                                    </p>
                                                    <p>
                                                        To ensure your safety and the optimal performance of your vehicle, please visit an authorized
                                                        service center to have the necessary inspection and repairs performed <b>free of charge</b>.
                                                    </p>
                                                    <p>
                                                        <b>Campaign period:</b> %s → %s
                                                    </p>
                                                    <p>
                                                        For more information or assistance, please contact our Customer Support Team.
                                                    </p>
                                                    <p>
                                                        Thank you for your continued trust in our brand.
                                                    </p>
                                                    <p>
                                                        Best regards,<br>
                                                        <b>OEM EV Warranty Management Team</b><br>
                                                        <i>OEM Electric Vehicle Division</i>
                                                    </p>
                                                </div>
                                                """,
                                campaign.getName(), campaign.getStartDate(), campaign.getEndDate());

                // Tạo email request (gửi 1 email, BCC toàn bộ khách hàng)
                EmailDetailsRequest emailDetails = EmailDetailsRequest.builder()
                                .recipient("noreply@oem.com") // địa chỉ To (không cần thật)
                                .bccList(customerEmails) // danh sách BCC khách hàng
                                .subject(subject)
                                .messageBody(message)
                                .build();

                emailService.sendHtmlMail(emailDetails);

                return "Recall notification sent (BCC) to " + customerEmails.size() +
                                " customers in campaign: " + campaign.getName();
        }

        @Override
        public List<VehicleInCampaignResponse> handleGetVehiclesInCampaignByServiceCenter(Long scId) {
                // Lấy danh sách CampaignVehicle theo ServiceCenter
                List<CampaignVehicle> campaignVehicles = campaignVehicleRepository.findByServiceCenterId(scId);

                // Kiểm tra rỗng
                if (campaignVehicles.isEmpty()) {
                        throw new IllegalArgumentException("No vehicles found for service center ID: " + scId);
                }

                // Map sang DTO VehicleInCampaignResponse
                List<VehicleInCampaignResponse> responses = campaignVehicles.stream()
                                .map(cv -> {
                                        var vehicle = cv.getVehicle();
                                        var campaign = cv.getServiceCampaign();
                                        var customer = vehicle.getCustomer();

                                        return VehicleInCampaignResponse.builder()
                                                        .campaignName(campaign.getName())
                                                        .vin(vehicle.getVin())
                                                        .customerName(customer != null ? customer.getName() : "")
                                                        .email(customer != null ? customer.getEmail() : "")
                                                        .phoneNumber(customer != null ? customer.getPhoneNumber() : "")
                                                        .address(customer != null ? customer.getAddress() : "")
                                                        .startDate(campaign.getStartDate())
                                                        .endDate(campaign.getEndDate())
                                                        .status(cv.getStatus())
                                                        .build();
                                })
                                .toList();

                // Trả kết quả về
                return responses;
        }
}