package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartRequestDetail;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.PartSupply;

@Repository
public interface PartRequestDetailRepository extends JpaRepository<PartRequestDetail, Long> {
    List<PartRequestDetail> findByPartRequest(PartSupply partSupply);
}