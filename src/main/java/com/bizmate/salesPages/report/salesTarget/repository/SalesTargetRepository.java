package com.bizmate.salesPages.report.salesTarget.repository;

import com.bizmate.salesPages.report.salesTarget.domain.SalesTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalesTargetRepository extends JpaRepository<SalesTarget, String> {
}
