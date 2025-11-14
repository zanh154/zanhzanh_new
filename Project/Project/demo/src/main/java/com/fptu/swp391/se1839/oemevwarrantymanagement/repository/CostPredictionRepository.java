package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.CostPrediction;

public interface CostPredictionRepository extends JpaRepository<CostPrediction, Long> {
    // Tìm theo service center
    java.util.List<CostPrediction> findByServiceCenterId(Long serviceCenterId);

    // Tìm theo tháng dự đoán
    java.util.List<CostPrediction> findByPredictionMonth(java.time.LocalDate month);

    // Tìm theo độ tin cậy
    java.util.List<CostPrediction> findByConfidence(String confidence);

    // Tìm theo khoảng thời gian
    java.util.List<CostPrediction> findByPredictionMonthBetween(java.time.LocalDate start, java.time.LocalDate end);

    // Tìm dự đoán mới nhất của mỗi service center
    @org.springframework.data.jpa.repository.Query("SELECT cp FROM CostPrediction cp WHERE cp.generatedAt = (SELECT MAX(cp2.generatedAt) FROM CostPrediction cp2 WHERE cp2.serviceCenter = cp.serviceCenter)")
    java.util.List<CostPrediction> findLatestPredictions();
}