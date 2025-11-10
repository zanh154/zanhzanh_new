package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "PartRequestDetail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartRequestDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "partSupplyId", nullable = false)
    PartSupply partRequest;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "partId", nullable = false)
    Part part; // Phụ tùng cần xin

    @Column(nullable = false)
    int requestedQuantity;

    @Column
    Integer approvedQuantity; // số lượng được duyệt (hãng có thể duyệt ít hơn)

    @Column(columnDefinition = "TEXT")
    String remark; // ghi chú của EVM Staff khi duyệt
}