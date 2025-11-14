package com.fptu.swp391.se1839.oemevwarrantymanagement.listener;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ActivityLog;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartClaim;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairOrder;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.RepairStep;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.WarrantyClaim;
import com.fptu.swp391.se1839.oemevwarrantymanagement.event.EntityCreatedEvent;
import com.fptu.swp391.se1839.oemevwarrantymanagement.event.EntityUpdatedEvent;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ActivityLogRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntityEventListener {

    private final ActivityLogRepository activityLogRepository;

    // 🟢 Khi tạo mới entity
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleEntityCreated(EntityCreatedEvent<?> event) {
        System.out.println(">>> 🔔 EntityCreatedEvent triggered for: " + event.getEntity().getClass().getSimpleName());
        System.out.println(">>> Transaction active? " + TransactionSynchronizationManager.isActualTransactionActive());

        Object entity = event.getEntity();

        if (entity instanceof WarrantyClaim claim) {
            ActivityLog log = ActivityLog.builder()
                    .title("New Warranty Claim Submitted")
                    .status("DRAFT")
                    .detail("Warranty claim #" + claim.getId() + " created")
                    .createdAt(LocalDateTime.now())
                    .claim(claim)
                    .meta("{\"event\":\"EntityCreatedEvent\"}")
                    .build();

            activityLogRepository.save(log);
            System.out.println(">>> 📝 ActivityLog for WarrantyClaim created!");
        }
    }

    // 🟡 Khi cập nhật entity (REQUIRES_NEW để log luôn được lưu)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleEntityUpdated(EntityUpdatedEvent<?> event) {
        Object entity = event.getEntity();

        if (entity instanceof WarrantyClaim claim) {
            ActivityLog log = ActivityLog.builder()
                    .title("Warranty Claim Status Updated")
                    .status("UPDATED")
                    .detail("Warranty claim #" + (claim.getId() != null ? claim.getId() : "[unsaved]") + " created")
                    .createdAt(LocalDateTime.now())
                    .claim(claim)
                    .meta("{\"event\":\"EntityUpdatedEvent\"}")
                    .build();

            activityLogRepository.save(log);
            System.out.println(">>> 🔧 ActivityLog for WarrantyClaim updated!");
        } else if (entity instanceof RepairOrder order) {
            ActivityLog log = ActivityLog.builder()
                    .title("Repair Order Updated")
                    .status(order.getStatus().name())
                    .detail("Repair order #" + order.getId() + " status changed to " + order.getStatus())
                    .createdAt(LocalDateTime.now())
                    .meta("{\"event\":\"EntityUpdatedEvent\"}")
                    .build();

            activityLogRepository.save(log);
            System.out.println(">>> 🧾 ActivityLog for RepairOrder saved!");
        } else if (entity instanceof PartClaim partClaim) {
            ActivityLog log = ActivityLog.builder()
                    .title("Part Claim Status Updated")
                    .status(partClaim.getStatus() != null ? partClaim.getStatus().name() : "UPDATED")
                    .detail("Part claim #" + (partClaim.getId() != null ? partClaim.getId() : "[unsaved]")
                            + " status changed to " + partClaim.getStatus())
                    .createdAt(LocalDateTime.now())
                    .meta("{\"event\":\"EntityUpdatedEvent\"}")
                    .build();

            activityLogRepository.save(log);
            System.out.println(">>> 📦 ActivityLog for PartClaim saved!");
        }

    }
}