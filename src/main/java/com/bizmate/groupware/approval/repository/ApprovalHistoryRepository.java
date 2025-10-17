package com.bizmate.groupware.approval.repository;

import com.bizmate.groupware.approval.domain.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {
    List<ApprovalHistory> findByDocIdOrderByActionTimestampAsc(String docId);
}
