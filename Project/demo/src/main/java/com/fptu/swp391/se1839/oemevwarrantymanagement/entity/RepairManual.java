package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "repair_manual")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairManual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "part_id", nullable = false)
    Part part;

    @Column(nullable = false)
    String model; // model xe / VIN hoặc kiểu xe

    @Column(nullable = false)
    int minQuantity; // số lượng tối thiểu hãng yêu cầu

    @Column(columnDefinition = "TEXT")
    String notes; // ví dụ: "thay cả bộ", "thay 2 bên"
}
