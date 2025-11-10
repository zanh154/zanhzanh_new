package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.util.List;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.AddVehicleRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CustomerRegisterRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.CustomerRegisterResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.CustomerSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RegisteredVehicleResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.VehicleInfoResponse;

public interface CustomerService {
    CustomerRegisterResponse registerCustomer(CustomerRegisterRequest req, Long createdBy, Long scId);

    CustomerRegisterResponse handleFindCustomerByVin(String vin);

    CustomerRegisterResponse updateCustomer(Long id, CustomerRegisterRequest req);

    CustomerRegisterResponse addVehicleForExistingCustomer(Long customerId, AddVehicleRequest req);

    List<RegisteredVehicleResponse> getAllRegisteredVehicles();

    List<CustomerSummaryResponse> getAllCustomerSummaries();

    List<VehicleInfoResponse> getVehiclesByCustomerId(Long customerId);

    List<CustomerSummaryResponse> findByKey(String key);

}