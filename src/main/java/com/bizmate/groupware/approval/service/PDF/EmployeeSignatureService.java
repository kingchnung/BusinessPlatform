package com.bizmate.groupware.approval.service.PDF;

import com.bizmate.groupware.approval.domain.PDF.EmployeeSignature;
import com.bizmate.groupware.approval.repository.PDF.EmployeeSignatureRepository;
import com.bizmate.hr.domain.Employee;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeSignatureService {

    private final EmployeeSignatureRepository signatureRepository;

    public Optional<EmployeeSignature> findByEmployee(Employee employee) {
        return signatureRepository.findByEmployeeEmpId(employee.getEmpId());
    }

    public String getSignaturePath(Employee employee) {
        return signatureRepository.findByEmployeeEmpId(employee.getEmpId())
                .map(sig -> {
                    try {
                        ClassPathResource resource = new ClassPathResource("signatures/" + sig.getSignImagePath());
                        return resource.getFile().getAbsolutePath();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .orElse(null);
    }
}
