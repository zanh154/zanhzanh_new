package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.util.List;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.VehicleRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllVehicleResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetRegisteredVehicleResponse;

public interface VehicleService {
    // void hanldeAddVehicle(Vehicle vehicle);
    GetAllVehicleResponse getAllVehicles();

    List<GetRegisteredVehicleResponse> handleFindRegisteredVehicleByPhone(VehicleRequest request);

    GetAllVehicleResponse handleFindVehicleByPhone(String phone);
}
