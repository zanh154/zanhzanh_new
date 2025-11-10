package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.AllEvidenceRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.AllEvidenceResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairOrder;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairOrderEvidence;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairOrderEvidenceRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairOrderRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.UserRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.RepairOrderEvidenceService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairOrderEvidenceServiceImpl implements RepairOrderEvidenceService {

    final RepairOrderEvidenceRepository evidenceRepository;
    final RepairOrderRepository orderRepository;
    final UserRepository userRepository;

    // Lưu file trên Docker, trả Base64 (chỉ dùng cho front-end hiển thị)
    public List<String> saveAttachmentsToDocker(Long repairOrderId, MultipartFile[] attachments) throws IOException {
        List<String> attachmentBase64 = new ArrayList<>();
        if (attachments == null || attachments.length == 0)
            return attachmentBase64;

        Path uploadDir = Paths.get("/uploads/repair_orders/", String.valueOf(repairOrderId));
        Files.createDirectories(uploadDir);

        for (MultipartFile file : attachments) {
            if (file == null || file.isEmpty())
                continue;

            byte[] bytes = file.getBytes();
            String original = Optional.ofNullable(file.getOriginalFilename())
                    .map(Paths::get)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .orElse("file");
            String filename = System.currentTimeMillis() + "_" + UUID.randomUUID() + "_" + original;
            Path filePath = uploadDir.resolve(filename);

            Files.write(filePath, bytes);

            String base64 = Base64.getEncoder().encodeToString(bytes);
            String base64WithPrefix = "data:" +
                    Optional.ofNullable(file.getContentType()).orElse("application/octet-stream") +
                    ";base64," + base64;
            attachmentBase64.add(base64WithPrefix);
        }
        return attachmentBase64;
    }

    @Override
    @Transactional
    public List<RepairOrderEvidence> handleCreateEvidence(long repairOrderId, AllEvidenceRequest request, long userId) {
        List<RepairOrderEvidence> result = new ArrayList<>();
        RepairOrder ro = orderRepository.findById(repairOrderId).orElseThrow(
                () -> new NoSuchElementException("Repair order: " + repairOrderId + " not found!"));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("Repair order: " + repairOrderId + " not found!"));
        String signature = request.getSignature();
        String otpCode = request.getOtpCode();
        String notes = request.getNotes();

        // 1️⃣ Lưu chữ ký online
        if (signature != null && !signature.isEmpty()) {
            RepairOrderEvidence evidence = RepairOrderEvidence.builder()
                    .repairOrder(ro)
                    .evidenceType("SIGNATURE")
                    .signature(signature)
                    .createdBy(user)
                    .build();
            evidenceRepository.save(evidence);
            result.add(evidence);
        }

        // 2️⃣ Lưu OTP / notes
        if ((otpCode != null && !otpCode.isEmpty()) || (notes != null && !notes.isEmpty())) {
            RepairOrderEvidence evidence = RepairOrderEvidence.builder()
                    .repairOrder(ro)
                    .evidenceType("NOTE")
                    .otpCode(otpCode)
                    .notes(notes)
                    .createdBy(user)
                    .build();
            evidenceRepository.save(evidence);
            result.add(evidence);
        }

        return result;
    }

    @Override
    public List<AllEvidenceResponse> getEvidenceByRepairOrderId(Long repairOrderId) throws IOException {
        List<AllEvidenceResponse> result = new ArrayList<>();

        // 1️⃣ Lấy tất cả evidence từ DB
        List<RepairOrderEvidence> evidences = evidenceRepository.findByRepairOrderId(repairOrderId);
        for (RepairOrderEvidence e : evidences) {
            result.add(AllEvidenceResponse.builder()
                    .id(e.getId())
                    .evidenceType(e.getEvidenceType())
                    .signature(e.getSignature())
                    .otpCode(e.getOtpCode())
                    .notes(e.getNotes())
                    .createdById(e.getCreatedBy() != null ? e.getCreatedBy().getId() : null)
                    .createdByName(e.getCreatedBy() != null ? e.getCreatedBy().getName() : null)
                    .createdAt(e.getCreatedAt() != null ? e.getCreatedAt().toString() : null)
                    .build());
        }

        // 2️⃣ Lấy tất cả file ảnh/video trực tiếp từ folder Docker
        Path uploadDir = Paths.get("/uploads/repair_orders/", String.valueOf(repairOrderId));
        if (Files.exists(uploadDir) && Files.isDirectory(uploadDir)) {
            for (File file : uploadDir.toFile().listFiles()) {
                if (file.isFile()) {
                    byte[] data = Files.readAllBytes(file.toPath());
                    String base64 = Base64.getEncoder().encodeToString(data);
                    String imageDataUrl = "data:" + Files.probeContentType(file.toPath()) + ";base64," + base64;

                    result.add(AllEvidenceResponse.builder()
                            .id(-1L) // chưa có ID trong DB
                            .evidenceType("PHOTO") // giả định là ảnh
                            .url(imageDataUrl)
                            .createdById(null)
                            .createdByName(null)
                            .createdAt(null)
                            .build());
                }
            }
        }

        return result;
    }

}
