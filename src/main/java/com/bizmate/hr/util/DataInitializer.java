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
        log.info("▶▶▶ DataInitializer 실행 시작: 기본 코드 및 정합성 점검");

        // 1️⃣ 기본 코드 데이터 세팅 (중복 방지)
        initBaseData();

        // 2️⃣ 직원이 없을 경우 샘플 데이터 자동 생성
        initDefaultEmployeesAndUsers();

        // 3️⃣ Employee ↔ User 정합성 점검
        syncEmployeesAndUsers();

        log.info("✅ DataInitializer 실행 완료 (코드 + 정합성 보정 완료)");
    }



    // =========================================================
    // 1️⃣ 기본 코드/부서 초기화 (중복 방지 로직 유지)
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

    // =========================================================
    // 2️⃣ 직원(Employee) + 사용자(UserEntity) 자동 생성 (비어있을 때만)
    // =========================================================
    private void initDefaultEmployeesAndUsers() {
        if (employeeRepository.count() > 0) {
            log.info("✅ 기존 직원 데이터가 존재하므로 샘플 생성 생략");
            return;
        }

        log.info("🚀 직원 데이터가 없으므로 기본 샘플 3명을 생성합니다.");

        Department deptMgmt = departmentRepository.findByDeptCode("10").orElseThrow();
        Department deptDev1 = departmentRepository.findByDeptCode("31").orElseThrow();
        Position posCEO = positionRepository.findByPositionName("CEO").orElseThrow();
        Position posManager = positionRepository.findByPositionName("팀장").orElseThrow();
        Position posStaff = positionRepository.findByPositionName("사원").orElseThrow();
        Grade gradeExec = gradeRepository.findByGradeName("임원").orElseThrow();
        Grade gradeStaff = gradeRepository.findByGradeName("사원/대리").orElseThrow();

        Role roleCEO = roleRepository.findByRoleName("CEO").orElseThrow();
        Role roleManager = roleRepository.findByRoleName("MANAGER").orElseThrow();
        Role roleEmployee = roleRepository.findByRoleName("EMPLOYEE").orElseThrow();

        // 👔 CEO
        Employee ceo = createEmployee("5010001", "김철수", deptMgmt, posCEO, gradeExec, "ACTIVE");
        createUserAccount(ceo, roleCEO, "ceo");

        // 👩‍💼 팀장
        Employee manager = createEmployee("5031001", "이영희", deptDev1, posManager, gradeStaff, "ACTIVE");
        createUserAccount(manager, roleManager, null);

        // 👨‍💻 사원
        Employee staff = createEmployee("5031002", "박민수", deptDev1, posStaff, gradeStaff, "ACTIVE");
        createUserAccount(staff, roleEmployee, null);

        log.info("✅ 기본 직원 3명 및 UserEntity 생성 완료");
    }

    // =========================================================
    // 2️⃣ Employee ↔ User 동기화 (누락 생성 + 복제 필드 업데이트)
    // =========================================================
    private void syncEmployeesAndUsers() {
        log.info("▶ Employee ↔ User 데이터 정합성 점검 시작");

        List<Employee> allEmployees = employeeRepository.findAll();
        if (allEmployees.isEmpty()) {
            log.warn("직원 데이터가 존재하지 않아 정합성 점검을 건너뜁니다.");
            return;
        }

        int created = 0;
        int updated = 0;

        Role defaultRole = roleRepository.findByRoleName("EMPLOYEE")
                .orElseThrow(() -> new IllegalStateException("기본 역할 'EMPLOYEE'가 없습니다."));

        for (Employee emp : allEmployees) {
            Optional<UserEntity> optUser = userRepository.findByEmployee(emp);

            if (optUser.isEmpty()) {
                // 🟢 직원은 있는데 User가 없을 경우 → 자동 생성
                createUserAccount(emp, defaultRole, null);
                created++;
            } else {
                // 🟢 둘 다 있을 경우 → 복제 필드 동기화
                UserEntity user = optUser.get();
                boolean changed = false;

                if (!Objects.equals(user.getEmpName(), emp.getEmpName())) {
                    user.setEmpName(emp.getEmpName());
                    changed = true;
                }
                if (!Objects.equals(user.getEmail(), emp.getEmail())) {
                    user.setEmail(emp.getEmail());
                    changed = true;
                }
                if (!Objects.equals(user.getPhone(), emp.getPhone())) {
                    user.setPhone(emp.getPhone());
                    changed = true;
                }
                if (emp.getDepartment() != null && !Objects.equals(user.getDeptName(), emp.getDepartment().getDeptName())) {
                    user.setDeptName(emp.getDepartment().getDeptName());
                    changed = true;
                }
                if (emp.getPosition() != null && !Objects.equals(user.getPositionName(), emp.getPosition().getPositionName())) {
                    user.setPositionName(emp.getPosition().getPositionName());
                    changed = true;
                }
                if (emp.getDepartment() != null && !Objects.equals(user.getDeptCode(), emp.getDepartment().getDeptCode())) {
                    user.setDeptCode(emp.getDepartment().getDeptCode());
                    changed = true;
                }

                if (changed) {
                    user.setUpdDate(LocalDateTime.now());
                    userRepository.save(user);
                    updated++;
                }
            }
        }

        log.info("✅ 동기화 완료: 신규 User {}건 생성, 기존 User {}건 갱신", created, updated);
    }

    // =========================================================
    // 3️⃣ 헬퍼 메서드 영역 (Permission, Role, Position 등)
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
                .orElseGet(() -> {
                    log.info(" - Position '{}' 생성", name);
                    return positionRepository.save(Position.builder()
                            .positionName(name)
                            .description(desc)
                            .isUsed("Y")
                            .build());
                });
    }

    private Grade createGrade(String name, Integer order) {
        return gradeRepository.findByGradeName(name)
                .orElseGet(() -> {
                    log.info(" - Grade '{}' 생성", name);
                    return gradeRepository.save(Grade.builder()
                            .gradeName(name)
                            .gradeOrder(order)
                            .isUsed("Y")
                            .build());
                });
    }

    private Department createDepartment(String code, String name, Department parent) {
        return departmentRepository.findByDeptCode(code)
                .orElseGet(() -> {
                    log.info(" - Department '{}' ({}) 생성", name, code);
                    Department dept = Department.builder()
                            .deptCode(code)
                            .deptName(name)
                            .parentDepartment(parent)
                            .isUsed("Y")
                            .creDate(LocalDateTime.now())
                            .build();
                    return departmentRepository.save(dept);
                });
    }

    // =========================================================
    // 4️⃣ User/Employee 생성 로직
    // =========================================================

    private String generateEmpNo(String deptCode) {
        if (deptCode == null || deptCode.length() != 2) {
            throw new IllegalArgumentException("유효하지 않은 부서 코드입니다.");
        }

        int nextSerial = deptSerialCounter.getOrDefault(deptCode, 0) + 1;
        deptSerialCounter.put(deptCode, nextSerial);

        String serialNumber = String.format("%03d", nextSerial);
        return COMPANY_CODE + deptCode + serialNumber;
    }

    private String generateRandomPhone() {
        return String.format("010-%04d-%04d",
                random.nextInt(9000) + 1000,
                random.nextInt(9000) + 1000);
    }

    private Employee createEmployee(String empNo, String name, Department dept, Position pos, Grade grade, String status) {
        String email = empNo + "@bizmate.com";

        Employee emp = Employee.builder()
                .empNo(empNo)
                .empName(name)
                .department(dept)
                .position(pos)
                .grade(grade)
                .status(status)
                .email(email)
                .phone(generateRandomPhone())
                .startDate(LocalDate.now())
                .creDate(LocalDateTime.now())
                .build();

        return employeeRepository.save(emp);
    }

    private UserEntity createUserAccount(Employee employee, Role role, String fixedUsername) {
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

                // 복제 필드 초기화
                .empName(employee.getEmpName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .deptName(employee.getDepartment() != null ? employee.getDepartment().getDeptName() : null)
                .positionName(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
                .deptCode(employee.getDepartment() != null ? employee.getDepartment().getDeptCode() : null)

                .build();

        return userRepository.save(user);
    }
}
