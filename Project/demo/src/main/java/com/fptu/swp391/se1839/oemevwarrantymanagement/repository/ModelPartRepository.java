package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.ModelPart;

@Repository
public interface ModelPartRepository extends JpaRepository<ModelPart, Long> {

    List<ModelPart> findByModelIdAndPartPartCategoryIgnoreCase(long modelId, String partCategory);

    List<ModelPart> findByModelId(long modelId);
}
