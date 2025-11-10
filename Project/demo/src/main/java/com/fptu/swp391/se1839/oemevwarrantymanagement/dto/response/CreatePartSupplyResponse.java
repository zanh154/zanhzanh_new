package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDateTime;
import java.util.List;

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
public class CreatePartSupplyResponse {
    boolean success;
    String message;
    Long partSupplyId;
    LocalDateTime createdDate;
    List<String> requestedParts;
}