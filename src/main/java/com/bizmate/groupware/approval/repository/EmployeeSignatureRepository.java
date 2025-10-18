package com.bizmate.groupware.approval.repository;

import com.bizmate.groupware.approval.domain.EmployeeSignature;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeSignatureRepository extends JpaRepository<EmployeeSignature, Long> {
    Optional<EmployeeSignature> findByEmployeeEmpId(Long empId);

    Optional<EmployeeSignature> findByEmployeeEmpNo(String empNo);
}
