package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "PartRequest")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartSupply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "serviceCenterId", nullable = false)
    ServiceCenter serviceCenter; // SC gửi yêu cầu

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "createdBy", nullable = false)
    User createdBy; // SC Staff tạo yêu cầu

    @Column(nullable = false)
    LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    Status status;

    public enum Status {
        PENDING, APPROVED, REJECTED, COMPLETED
    }

    @Column(columnDefinition = "TEXT")
    String note; // ghi chú thêm, lý do xin thêm phụ tùng

    @OneToMany(mappedBy = "partRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    Set<PartRequestDetail> details = new HashSet<>();
}