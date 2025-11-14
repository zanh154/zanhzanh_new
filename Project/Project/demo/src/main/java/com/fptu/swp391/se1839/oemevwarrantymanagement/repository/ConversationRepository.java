package com.fptu.swp391.se1839.oemevwarrantymanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByStaffIdAndTechnicianId(Long staffId, Long technicianId);

    @Query("""
                SELECT DISTINCT c
                FROM Conversation c
                WHERE c.staffId = :userId OR c.technicianId = :userId
            """)
    List<Conversation> findDistinctByUser(Long userId);
}
