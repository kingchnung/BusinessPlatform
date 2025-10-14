package com.bizmate.hr.util;

import com.bizmate.hr.domain.*;
import com.bizmate.hr.domain.code.Grade;
import com.bizmate.hr.domain.code.Position;
import com.bizmate.hr.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private static final String COMPANY_CODE = "50";
    private static final String INITIAL_PASSWORD = "1234";

    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final GradeRepository gradeRepository;
    private final DepartmentRepository departmentRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final Random random = new Random();
    private final Map<String, Integer> deptSerialCounter = new HashMap<>();

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("▶▶▶ DataInitializer 실행 시작");

        initBaseData();

//        if (employeeRepository.count() > 0) {
//            clearEmployeeData();  // 🔹 이 위치에 추가
//        }

        // 직원이 없을 경우 기본 세트 생성
        if (employeeRepository.count() == 0) {
            initDefaultEmployees();
        }

        // Employee ↔ User 정합성 점검 및 보정
        syncEmployeesAndUsers();

        log.info("✅ DataInitializer 실행 완료");
    }

    // =========================================================
    // 1️⃣ 기본 코드/부서 초기화
    // =========================================================
    private void initBaseData() {
        log.info("▶ 기본 코드 데이터 확인 중...");

        Permission permSysAdmin = createPermission("sys:admin", "시스템 설정 및 관리 권한");
        Permission permDataReadAll = createPermission("data:read:all", "모든 부서 및 직원 데이터 조회");
        Permission permDataWriteAll = createPermission("data:write:all", "모든 직원 데이터 수정/삭제");
        Permission permDataReadSelf = createPermission("data:read:self", "본인 정보만 조회/수정");

        Role roleCEO = createRole("CEO", "최고 경영자 역할", Set.of(permSysAdmin, permDataReadAll, permDataWriteAll, permDataReadSelf));
        Role roleMANAGER = createRole("MANAGER", "팀 관리자 및 1차 결재 역할", Set.of(permDataReadAll, permDataReadSelf));
        Role roleEMPLOYEE = createRole("EMPLOYEE", "일반 직원 역할", Set.of(permDataReadSelf));

        createPosition("CEO", "최고 의사 결정권자");
        createPosition("팀장", "팀 운영 및 관리 책임");
        createPosition("사원", "일반 실무자");

        createGrade("임원", 100);
        createGrade("부장/차장", 70);
        createGrade("사원/대리", 30);

        Department deptManagement = createDepartment("10", "경영관리부", null);
        Department deptSales = createDepartment("20", "영업부", null);
        Department deptDevelopment = createDepartment("30", "개발부", null);

        createDepartment("11", "경영지원팀", deptManagement);
        createDepartment("12", "회계팀", deptManagement);
        createDepartment("21", "영업팀", deptSales);
        createDepartment("31", "개발1팀", deptDevelopment);
        createDepartment("32", "개발2팀", deptDevelopment);
        createDepartment("33", "개발3팀", deptDevelopment);

        log.info("✅ 기본 코드/부서 데이터 점검 완료");
    }

//    private void clearEmployeeData() {
//        log.warn("⚠ 직원 및 관련 데이터 초기화 시작");
//
//        // 외래키 참조 순서대로 삭제 (존재하는 Repository에 맞춰 조정)
//        userRepository.deleteAll();                // UserEntity → Employee FK
//        departmentRepository.deleteAll();
//        employeeRepository.deleteAll();            // Employee 최종 삭제
//
//
//        log.info("✅ 직원 관련 데이터 초기화 완료");
//    }

    // =========================================================
    // 2️⃣ 직원/유저 정합성 점검 및 보정
    // =========================================================
    private void syncEmployeesAndUsers() {
        log.info("▶ Employee ↔ User 정합성 점검 시작");

        List<Employee> allEmployees = employeeRepository.findAll();
        if (allEmployees.isEmpty()) {
            log.warn("직원 데이터가 없어 정합성 점검을 건너뜁니다.");
            return;
        }

        int created = 0, updated = 0;

        Role defaultRole = roleRepository.findByRoleName("EMPLOYEE")
                .orElseThrow(() -> new IllegalStateException("기본 역할 'EMPLOYEE'가 없습니다."));

        for (Employee emp : allEmployees) {
            Optional<UserEntity> optUser = userRepository.findByEmployee(emp);

            if (optUser.isEmpty()) {
                createUserAccount(emp, defaultRole);
                created++;
            } else {
                UserEntity user = optUser.get();
                boolean changed = false;

                if (!Objects.equals(user.getEmpName(), emp.getEmpName())) {
                    user.setEmpName(emp.getEmpName()); changed = true;
                }
                if (!Objects.equals(user.getEmail(), emp.getEmail())) {
                    user.setEmail(emp.getEmail()); changed = true;
                }
                if (!Objects.equals(user.getPhone(), emp.getPhone())) {
                    user.setPhone(emp.getPhone()); changed = true;
                }
                if (emp.getDepartment() != null && !Objects.equals(user.getDeptName(), emp.getDepartment().getDeptName())) {
                    user.setDeptName(emp.getDepartment().getDeptName()); changed = true;
                }
                if (emp.getPosition() != null && !Objects.equals(user.getPositionName(), emp.getPosition().getPositionName())) {
                    user.setPositionName(emp.getPosition().getPositionName()); changed = true;
                }
                if (emp.getDepartment() != null && !Objects.equals(user.getDeptCode(), emp.getDepartment().getDeptCode())) {
                    user.setDeptCode(emp.getDepartment().getDeptCode()); changed = true;
                }

                if (changed) {
                    user.setUpdDate(LocalDateTime.now());
                    userRepository.save(user);
                    updated++;
                }
            }
        }

        log.info("✅ 정합성 완료: 신규 User {}건 생성, 기존 User {}건 갱신", created, updated);
    }

    // =========================================================
    // 3️⃣ 헬퍼 메서드들
    // =========================================================
    private Permission createPermission(String name, String desc) {
        return permissionRepository.findByPermName(name)
                .orElseGet(() -> {
                    log.info(" - Permission '{}' 생성", name);
                    return permissionRepository.save(Permission.builder()
                            .permName(name)
                            .description(desc)
                            .build());
                });
    }

    private Role createRole(String name, String desc, Set<Permission> perms) {
        return roleRepository.findByRoleName(name)
                .orElseGet(() -> {
                    log.info(" - Role '{}' 생성", name);
                    return roleRepository.save(Role.builder()
                            .roleName(name)
                            .description(desc)
                            .permissions(perms)
                            .build());
                });
    }

    private Position createPosition(String name, String desc) {
        return positionRepository.findByPositionName(name)
                .orElseGet(() -> positionRepository.save(Position.builder()
                        .positionName(name)
                        .description(desc)
                        .build()));
    }

    private Grade createGrade(String name, Integer order) {
        return gradeRepository.findByGradeName(name)
                .orElseGet(() -> gradeRepository.save(Grade.builder()
                        .gradeName(name)
                        .gradeOrder(order)
                        .build()));
    }

    private Department createDepartment(String code, String name, Department parent) {
        return departmentRepository.findByDeptCode(code)
                .orElseGet(() -> departmentRepository.save(Department.builder()
                        .deptCode(code)
                        .deptName(name)
                        .parentDept(parent)
                        .creDate(LocalDateTime.now())
                        .build()));
    }

    // =========================================================
    // 4️⃣ 직원/유저 생성
    // =========================================================
    private String generateEmpNo(String deptCode) {
        int nextSerial = deptSerialCounter.getOrDefault(deptCode, 0) + 1;
        deptSerialCounter.put(deptCode, nextSerial);
        return COMPANY_CODE + deptCode + String.format("%03d", nextSerial);
    }

    private String generateRandomPhone() {
        return String.format("010-%04d-%04d",
                random.nextInt(9000) + 1000,
                random.nextInt(9000) + 1000);
    }

    private Employee createEmployee(
            String empNo,
            String name,
            Department dept,
            Position pos,
            Grade grade,
            String status
    ) {
        String email = empNo + "@bizmate.com";
        String phone = generateRandomPhone();
        String address = "서울특별시 강남구 테헤란로 100";
        LocalDate birthDate = LocalDate.of(1990, random.nextInt(12) + 1, random.nextInt(28) + 1);
        String gender = random.nextBoolean() ? "M" : "F";


        Employee emp = Employee.builder()
                .empNo(empNo)
                .empName(name)
                .department(dept)
                .position(pos)
                .grade(grade)
                .status(status)
                .email(email)
                .phone(phone)
                .address(address)
                .birthDate(birthDate)
                .gender(gender)
                .startDate(LocalDate.now())
                .creDate(LocalDateTime.now())
                .build();

        return employeeRepository.save(emp);
    }

    private UserEntity createUserAccount(Employee employee, Role role) {
        Set<Role> roles = new HashSet<>(Collections.singletonList(role));

        UserEntity user = UserEntity.builder()
                .username(employee.getEmpNo().equals("5010001") ? "ceo" : employee.getEmpNo())
                .pwHash(passwordEncoder.encode(INITIAL_PASSWORD))
                .employee(employee)
                .isActive("Y")
                .isLocked("N")
                .failedCount(0)
                .creDate(LocalDateTime.now())
                .roles(roles)
                .empName(employee.getEmpName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .deptName(employee.getDepartment() != null ? employee.getDepartment().getDeptName() : null)
                .positionName(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
                .deptCode(employee.getDepartment() != null ? employee.getDepartment().getDeptCode() : null)
                .build();

        return userRepository.save(user);
    }

    // =========================================================
    // 5️⃣ 초기 직원 생성 (환경별 동일 보장)
    // =========================================================
    private void initDefaultEmployees() {
            log.info("▶ 기본 직원(30명) 자동 생성 시작");

            // ===== 공통 레퍼런스 엔티티 조회 =====
            Department deptMgmt = departmentRepository.findByDeptCode("10").orElseThrow();
            Department deptSupport = departmentRepository.findByDeptCode("11").orElseThrow();
            Department deptAccounting = departmentRepository.findByDeptCode("12").orElseThrow();
            Department deptSales = departmentRepository.findByDeptCode("21").orElseThrow();
            Department deptDev1 = departmentRepository.findByDeptCode("31").orElseThrow();
            Department deptDev2 = departmentRepository.findByDeptCode("32").orElseThrow();
            Department deptDev3 = departmentRepository.findByDeptCode("33").orElseThrow();

            Position posCEO = positionRepository.findByPositionName("CEO").orElseThrow();
            Position posManager = positionRepository.findByPositionName("팀장").orElseThrow();
            Position posEmployee = positionRepository.findByPositionName("사원").orElseThrow();

            Grade gradeExec = gradeRepository.findByGradeName("임원").orElseThrow();
            Grade gradeManager = gradeRepository.findByGradeName("부장/차장").orElseThrow();
            Grade gradeStaff = gradeRepository.findByGradeName("사원/대리").orElseThrow();

            Role roleCEO = roleRepository.findByRoleName("CEO").orElseThrow();
            Role roleMANAGER = roleRepository.findByRoleName("MANAGER").orElseThrow();
            Role roleEMPLOYEE = roleRepository.findByRoleName("EMPLOYEE").orElseThrow();

            // ===== CEO (1명) =====
            Employee ceo = createEmployee(generateEmpNo("10"), "홍길동", deptMgmt, posCEO, gradeExec, "재직");
            createUserAccount(ceo, roleCEO);  // 🔹 CEO 권한 부여

            // 팀장 6명
            createUserAccount(createEmployee(generateEmpNo("11"), "김지원", deptSupport, posManager, gradeManager, "재직"), roleMANAGER);
            createUserAccount(createEmployee(generateEmpNo("12"), "이회계", deptAccounting, posManager, gradeManager, "재직"), roleMANAGER);
            createUserAccount(createEmployee(generateEmpNo("21"), "박영업", deptSales, posManager, gradeManager, "재직"), roleMANAGER);
            createUserAccount(createEmployee(generateEmpNo("31"), "최개발", deptDev1, posManager, gradeManager, "재직"), roleMANAGER);
            createUserAccount(createEmployee(generateEmpNo("32"), "정개발", deptDev2, posManager, gradeManager, "재직"), roleMANAGER);
            createUserAccount(createEmployee(generateEmpNo("33"), "오개발", deptDev3, posManager, gradeManager, "재직"), roleMANAGER);

            // 일반 직원 (모두 EMPLOYEE)
            for (int i = 1; i <= 5; i++)
                createUserAccount(createEmployee(generateEmpNo("11"), "경영사원" + i, deptSupport, posEmployee, gradeStaff, "재직"), roleEMPLOYEE);

            for (int i = 1; i <= 3; i++)
                createUserAccount(createEmployee(generateEmpNo("12"), "회계사원" + i, deptAccounting, posEmployee, gradeStaff, "재직"), roleEMPLOYEE);

            for (int i = 1; i <= 4; i++)
                createUserAccount(createEmployee(generateEmpNo("21"), "영업사원" + i, deptSales, posEmployee, gradeStaff, "재직"), roleEMPLOYEE);

            for (int i = 1; i <= 4; i++)
                createUserAccount(createEmployee(generateEmpNo("31"), "개발1팀사원" + i, deptDev1, posEmployee, gradeStaff, "재직"), roleEMPLOYEE);

            for (int i = 1; i <= 3; i++)
                createUserAccount(createEmployee(generateEmpNo("32"), "개발2팀사원" + i, deptDev2, posEmployee, gradeStaff, "재직"), roleEMPLOYEE);

            for (int i = 1; i <= 4; i++)
                createUserAccount(createEmployee(generateEmpNo("33"), "개발3팀사원" + i, deptDev3, posEmployee, gradeStaff, "재직"), roleEMPLOYEE);

            log.info("✅ 기본 직원(30명) + 권한 매핑 완료");


    }
}
