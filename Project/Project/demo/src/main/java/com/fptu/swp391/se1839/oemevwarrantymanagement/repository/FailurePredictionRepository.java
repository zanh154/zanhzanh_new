package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.FailurePrediction;

public interface FailurePredictionRepository extends JpaRepository<FailurePrediction, Long> {
    // Tìm theo phụ tùng
    java.util.List<FailurePrediction> findByPartId(Long partId);

    // Tìm theo mức độ nghiêm trọng
    java.util.List<FailurePrediction> findBySeverity(String severity);

    // Tìm dự đoán còn hiệu lực
    java.util.List<FailurePrediction> findByValidUntilAfter(java.time.LocalDateTime date);

    // Tìm theo xác suất lỗi cao
    java.util.List<FailurePrediction> findByFailureProbabilityGreaterThan(Double threshold);
}