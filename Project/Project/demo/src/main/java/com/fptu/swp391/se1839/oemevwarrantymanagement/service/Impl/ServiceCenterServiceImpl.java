package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ServiceCenterResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ServiceCenter;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ServiceCenterRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.ServiceCenterService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceCenterServiceImpl implements ServiceCenterService {

    final ServiceCenterRepository serviceCenterRepository;

    @Override
    public ServiceCenter handleAddCenter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceCenter handleFindempolyee(User user) {
        return this.serviceCenterRepository.findById(user.getServiceCenter().getId())
                .orElseThrow(() -> new NoSuchElementException("No find Service Center"));
    }

    public List<ServiceCenterResponse> getAllSC() {
        List<ServiceCenter> serviceCenters = serviceCenterRepository.findAll();
        List<ServiceCenterResponse> serviceCenterResponses = new ArrayList<>();
        for (ServiceCenter serviceCenter : serviceCenters) {
            serviceCenterResponses.add(new ServiceCenterResponse(
                    serviceCenter.getId(),
                    serviceCenter.getName(),
                    serviceCenter.getAddress(),
                    serviceCenter.getPhoneNumber()));
        }
        return serviceCenterResponses;
    }
}
