package com.fptu.swp391.se1839.oemevwarrantymanagement.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    String name;

    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotEmpty(message = "Email cannot be empty")
    String email;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "\\d{10}", message = "Phone number must contain exactly 10 digits")
    String phoneNumber;

    @Column(nullable = false)
    @Builder.Default
    boolean requiresPasswordChange = false;

    @Column
    @Builder.Default
    LocalDate date = LocalDate.now();

    @Column
    @Builder.Default
    LocalDate lastLoginDate = null; // trong User

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must have at least 6 characters")
    String password;

    @Column(length = 50)
    @Builder.Default
    String provider = "LOCAL"; // "LOCAL" hoặc "GOOGLE"

    public enum WorkStatus {
        AVAILABLE, // Rảnh, có thể nhận xe mới
        BUSY, // Đang sửa xe
        ON_LEAVE, // Nghỉ phép
        OFF_DUTY // Hết ca, không làm việc
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    WorkStatus workStatus = WorkStatus.OFF_DUTY;

    @Column(length = 500)
    String avatar = null;

    public enum Role {
        ADMIN, TECHNICIAN, SC_STAFF, EVM_STAFF, SUPERVISOR;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    Role role;

    public enum Status {
        ACTIVE, INACTIVE, SUSPENDED
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    Status status;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "serviceCenterId", nullable = true)
    ServiceCenter serviceCenter;

    @OneToMany(mappedBy = "technical")
    @JsonIgnore
    @Builder.Default
    Set<RepairOrder> repairOrders = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @Builder.Default
    Set<InvalidToken> invalidTokens = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof User))
            return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name='" + name + '\'' + ", email='" + email + '\'' + ", phoneNumber='"
                + phoneNumber + '\'' + ", role=" + role + ", status=" + status + ", serviceCenterId="
                + (serviceCenter != null ? serviceCenter.getId() : null) + ", repairOrderCount="
                + (repairOrders != null ? repairOrders.size() : 0) + '}';
    }
}
