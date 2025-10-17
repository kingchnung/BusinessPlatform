package com.bizmate.hr.service;

import com.bizmate.hr.domain.Department;
import com.bizmate.hr.dto.department.*;
import com.bizmate.hr.repository.DepartmentRepository;
import com.bizmate.hr.service.DepartmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    /** 📊 부서현황 조회 */
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentOverviewDTO> getDepartmentOverview() {
        List<Department> departments = departmentRepository.findAllByOrderByDeptCodeAsc();

        return departments.stream().map(dept -> {
            int empCount = dept.getEmployees().size();
            double avgAge = dept.getEmployees().stream()
                    .filter(e -> e.getBirthDate() != null)
                    .mapToInt(e -> Period.between(e.getBirthDate(), LocalDate.now()).getYears())
                    .average().orElse(0);
            double avgYears = dept.getEmployees().stream()
                    .filter(e -> e.getStartDate() != null)
                    .mapToInt(e -> Period.between(e.getStartDate(), LocalDate.now()).getYears())
                    .average().orElse(0);

            return DepartmentOverviewDTO.builder()
                    .deptId(dept.getDeptId())
                    .deptName(dept.getDeptName())
                    .deptCode(dept.getDeptCode())
                    .parentDeptId(dept.getParentDept() != null ? dept.getParentDept().getDeptId() : null)
                    .employeeCount(empCount)
                    .avgAge(Math.round(avgAge * 10) / 10.0)
                    .avgYears(Math.round(avgYears * 10) / 10.0)
                    .build();
        }).collect(Collectors.toList());
    }

    /** 📋 전체 부서 조회 */
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAllByOrderByDeptCodeAsc()
                .stream()
                .map(DepartmentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /** 📋 부서 상세조회 */
    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentDetail(Long deptId) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다. ID=" + deptId));
        return DepartmentResponseDTO.fromEntity(dept);
    }

    /** 🏗️ 부서 생성 */
    @Override
    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentCreateRequestDTO dto) {
        String nextCode = generateNextDeptCode(dto.getParentDeptId());

        Department department = Department.builder()
                .deptCode(nextCode)
                .deptName(dto.getDeptName())
                .parentDept(dto.getParentDeptId() != null
                        ? departmentRepository.findById(dto.getParentDeptId()).orElse(null)
                        : null)
                .isUsed("Y")
                .build();

        departmentRepository.save(department);
        log.info("✅ 신규 부서 생성 완료: {} ({})", department.getDeptName(), department.getDeptCode());
        return DepartmentResponseDTO.fromEntity(department);
    }

    /** ✏️ 부서 수정 */
    @Override
    @Transactional
    public DepartmentResponseDTO updateDepartment(Long deptId, DepartmentUpdateRequestDTO dto) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다. ID=" + deptId));

        dept.setDeptName(dto.getDeptName());
        dept.setIsUsed(dto.getIsUsed());
        departmentRepository.save(dept);
        return DepartmentResponseDTO.fromEntity(dept);
    }

    /** 🗑️ 부서 삭제 */
    @Override
    @Transactional
    public void deleteDepartment(Long deptId) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다. ID=" + deptId));
        departmentRepository.delete(dept);
        log.info("🗑️ 부서 삭제 완료: {}", dept.getDeptName());
    }

    /** 🧮 부서코드 자동 생성 규칙 */
    private String generateNextDeptCode(Long parentDeptId) {
        if (parentDeptId == null) {
            // 상위 부서 없음 → 본부 (10, 20, 30...)
            int maxCode = departmentRepository.findAllByOrderByDeptCodeAsc().stream()
                    .filter(d -> d.getDeptCode().endsWith("0"))
                    .mapToInt(d -> Integer.parseInt(d.getDeptCode()))
                    .max().orElse(0);
            return String.valueOf(maxCode + 10);
        } else {
            // 하위 부서 (팀)
            Department parent = departmentRepository.findById(parentDeptId)
                    .orElseThrow(() -> new EntityNotFoundException("상위 부서를 찾을 수 없습니다."));
            String prefix = parent.getDeptCode().substring(0, 1);
            int maxCode = departmentRepository.findAllByOrderByDeptCodeAsc().stream()
                    .filter(d -> d.getDeptCode().startsWith(prefix))
                    .mapToInt(d -> Integer.parseInt(d.getDeptCode()))
                    .max().orElse(Integer.parseInt(prefix + "0"));
            return String.valueOf(maxCode + 1);
        }
    }
}
