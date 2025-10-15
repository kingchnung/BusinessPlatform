package com.bizmate.hr.util;

import com.bizmate.hr.domain.*;
import com.bizmate.hr.domain.code.Grade;
import com.bizmate.hr.domain.code.Position;
import com.bizmate.hr.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component; // ★ @Configuration 대신 @Component로 변경
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component // ★ 이 클래스를 Spring Bean으로 등록하여 CommandLineRunner로 실행되게 함
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    // ★★★ 상수 정의
    private static final String COMPANY_CODE = "50";
    private static final String INITIAL_PASSWORD = "1234";

    // ★★★ Repository 주입
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final GradeRepository gradeRepository;
    private final DepartmentRepository departmentRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    // 부서별 고유번호 카운터를 위한 맵
    private final Map<String, Integer> deptSerialCounter = new HashMap<>();

    /**
     * empNo 생성 핵심 로직: 회사코드(50) + 부서코드(XX) + 고유번호(XXX)
     * @param deptCode 부서 코드 (예: "11")
     * @return 7자리 empNo (예: "5011001")
     */
    private String generateEmpNo(String deptCode) {
        if (deptCode == null || deptCode.length() != 2) {
            throw new IllegalArgumentException("유효하지 않은 부서 코드입니다.");
        }

        int nextSerial = deptSerialCounter.getOrDefault(deptCode, 0) + 1;
        deptSerialCounter.put(deptCode, nextSerial);

        String serialNumber = String.format("%03d", nextSerial);

        return COMPANY_CODE + deptCode + serialNumber;
    }

    /**
     * ★★★ CommandLineRunner의 run() 메서드를 구현 (애플리케이션 시작 시 트랜잭션 내에서 실행)
     * @Bean public CommandLineRunner initData() { ... } 로직을 이리로 옮겼습니다.
     */
    @Override
    @Transactional // ★ 트랜잭션 적용
    public void run(String... args) throws Exception {
        log.info("▶▶▶ DataInitializer 실행: 초기 환경 및 사용자 계정 설정 시작");

        // ----------------------------------------------------
        // 0. 초기화 대상 데이터 존재 여부 확인 후 건너뛰기
        // ----------------------------------------------------

        // 사용자가 이미 생성되었다면 (즉, 직원 데이터가 생성되었다면) 전체 초기화를 건너뜁니다.
        if (userRepository.count() > 0) {
            log.warn("DB에 사용자 데이터가 이미 존재합니다. 초기 직원 데이터 생성을 건너뜁니다.");
            return;
        }

        // ----------------------------------------------------
        // 1. Permission & 2. Role 생성 (중복 방지 로직 적용됨)
        // ----------------------------------------------------
        Permission permSysAdmin = createPermission("sys:admin", "시스템 설정 및 관리 권한");
        Permission permDataReadAll = createPermission("data:read:all", "모든 부서 및 직원 데이터 조회");
        Permission permDataWriteAll = createPermission("data:write:all", "모든 직원 데이터 수정/삭제");
        Permission permDataReadSelf = createPermission("data:read:self", "본인 정보만 조회/수정");

        // Role 생성 시, 위에서 생성/조회한 Permission들을 사용합니다.
        Role roleCEO = createRole("CEO", "최고 경영자 역할", Set.of(permSysAdmin, permDataReadAll, permDataWriteAll, permDataReadSelf));
        Role roleMANAGER = createRole("MANAGER", "팀 관리자 및 1차 결재 역할", Set.of(permDataReadAll, permDataReadSelf));
        Role roleEMPLOYEE = createRole("EMPLOYEE", "일반 직원 역할", Set.of(permDataReadSelf));

        // ----------------------------------------------------
        // 3. Position & Grade 코드 생성 (중복 방지 로직 적용됨)
        // ----------------------------------------------------
        Position posCEO = createPosition("CEO", "최고 의사 결정권자");
        Position posManager = createPosition("팀장", "팀 운영 및 관리 책임");
        Position posEmployee = createPosition("사원", "일반 실무자");

        Grade gradeExec = createGrade("임원", 100);
        Grade gradeManager = createGrade("부장/차장", 70);
        Grade gradeStaff = createGrade("사원/대리", 30);


        // ----------------------------------------------------
        // 4. Department (부서) 생성 및 계층 연결 (중복 방지 로직 적용됨)
        // ----------------------------------------------------
        // 부서는 부모 부서를 참조할 수 있으므로, 조회된 엔티티를 사용하여 연결합니다.
        Department deptManagement = createDepartment("10", "경영관리부", null);
        Department deptSales = createDepartment("20", "영업부", null);
        Department deptDevelopment = createDepartment("30", "개발부", null);

        Department deptManagementTeam = createDepartment("11", "경영지원팀", deptManagement);
        Department deptAccountingTeam = createDepartment("12", "회계팀", deptManagement);
        Department deptSalesTeam = createDepartment("21", "영업팀", deptSales);
        Department deptDev1Team = createDepartment("31", "개발1팀", deptDevelopment);
        Department deptDev2Team = createDepartment("32", "개발2팀", deptDevelopment);
        Department deptDev3Team = createDepartment("33", "개발3팀", deptDevelopment);


        // ----------------------------------------------------
        // 5. Employee (직원) 및 UserEntity (계정) 생성 (총 30명)
        // ----------------------------------------------------

        // 주의: deptSerialCounter는 재구동 시 초기화되므로,
        // 현재 DB 상태와 관계없이 001부터 다시 생성합니다. (Employee가 없는 상태이므로 문제없음)

        // A. CEO 계정 (1명) - empNo: 5010001
        Employee ceoEmployee = createEmployee(
                generateEmpNo(deptManagement.getDeptCode()),
                "최고경영자",
                deptManagement,
                posCEO, gradeExec, "active"
        );
        createUserAccount(ceoEmployee, roleCEO);
        // 부서 엔티티를 다시 저장할 때, Manager FK 연결을 위해 createDepartment에서 반환된 객체를 사용해야 안전합니다.
        // 여기서는 다시 save()를 호출하여 Manager FK를 업데이트합니다.
        deptManagement.setManager(ceoEmployee);
        departmentRepository.save(deptManagement);

        // B. 팀장 및 팀원 생성
        Map<Department, Integer> teamDistribution = Map.of(
                deptManagementTeam, 2, deptAccountingTeam, 2, deptSalesTeam, 3,
                deptDev1Team, 6, deptDev2Team, 5, deptDev3Team, 5
        );

        int managerCount = 0;
        int employeeCount = 0;

        for (Map.Entry<Department, Integer> entry : teamDistribution.entrySet()) {
            Department dept = entry.getKey();
            int numEmployees = entry.getValue();

            // 1) 팀장 생성 (1명) - empNo: 50XX001
            String managerEmpName = dept.getDeptName().replace("팀", "팀장");
            Employee managerEmployee = createEmployee(
                    generateEmpNo(dept.getDeptCode()),
                    managerEmpName,
                    dept,
                    posManager, gradeManager, "active"
            );
            createUserAccount(managerEmployee, roleMANAGER);

            dept.setManager(managerEmployee); // ★ 부서 엔티티에 Manager FK 연결
            departmentRepository.save(dept);
            managerCount++;

            // 2) 일반 직원 생성 - empNo: 50XX002, 50XX003 ...
            for (int i = 0; i < numEmployees; i++) {
                String employeeName = dept.getDeptName().substring(0, 2) + "팀원" + (i + 1);
                Employee employee = createEmployee(
                        generateEmpNo(dept.getDeptCode()),
                        employeeName,
                        dept,
                        posEmployee, gradeStaff, "active"
                );
                createUserAccount(employee, roleEMPLOYEE);
                employeeCount++;
            }
        }

        log.info("▶ DataInitializer 실행 완료. CEO(1명), 팀장({}), 일반직원({}) 생성 완료. 총 {}명.", managerCount, employeeCount, 1 + managerCount + employeeCount);
        log.info("▶ 초기 로그인 계정: ID: ceo, PW: {}", INITIAL_PASSWORD);
    }


    // ----------------------------------------------------
    // 6. Helper Methods (엔티티 생성 및 중복 확인 후 저장)
    // ----------------------------------------------------

    private Permission createPermission(String name, String desc) {
        // ★ 중복 확인 로직 적용
        return permissionRepository.findByPermName(name)
                .orElseGet(() -> {
                    log.info(" - Permission '{}' 생성", name);
                    return permissionRepository.save(Permission.builder().permName(name).description(desc).build());
                });
    }

    private Role createRole(String name, String desc, Set<Permission> perms) {
        // ★ 중복 확인 로직 적용
        return roleRepository.findByRoleName(name)
                .orElseGet(() -> {
                    log.info(" - Role '{}' 생성", name);
                    // Role이 존재하지 않으면 새로운 Role을 생성하고, 위에서 조회/생성된 Perms를 연결합니다.
                    return roleRepository.save(Role.builder().roleName(name).description(desc).permissions(perms).build());
                });
    }

    private Position createPosition(String name, String desc) {
        // ★ 중복 확인 로직 적용
        return positionRepository.findByPositionName(name)
                .orElseGet(() -> {
                    log.info(" - Position '{}' 생성", name);
                    return positionRepository.save(Position.builder().positionName(name).description(desc).isUsed("Y").build());
                });
    }

    private Grade createGrade(String name, Integer order) {
        // ★ 중복 확인 로직 적용
        return gradeRepository.findByGradeName(name)
                .orElseGet(() -> {
                    log.info(" - Grade '{}' 생성", name);
                    return gradeRepository.save(Grade.builder().gradeName(name).gradeOrder(order).isUsed("Y").build());
                });
    }

    private Department createDepartment(String code, String name, Department parent) {
        // ★ 중복 확인 로직 적용 (deptCode는 Unique하다고 가정)
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
    private final Random random = new Random();

    /**
     * 010-xxxx-xxxx 형식의 랜덤 전화번호를 생성합니다.
     */
    private String generateRandomPhone() {
        StringBuilder phone = new StringBuilder("010-");

        // 중간 4자리 (1000 ~ 9999)
        phone.append(String.format("%04d", random.nextInt(9000) + 1000)).append("-");

        // 마지막 4자리 (1000 ~ 9999)
        phone.append(String.format("%04d", random.nextInt(9000) + 1000));

        return phone.toString();
    }

    // Employee와 UserEntity는 초기화 시점에만 생성되므로, 별도의 중복 확인 로직 없이 저장만 합니다.
    private Employee createEmployee(String empNo, String name, Department dept, Position pos, Grade grade, String status) {
        String initialEmail = empNo + "@bizmate.com";
        String initialPhone = generateRandomPhone(); // 헬퍼 메서드 호출

        Employee emp = Employee.builder()
                .empNo(empNo)
                .empName(name)
                .department(dept)
                .position(pos)
                .grade(grade)
                .status(status)
                .email(initialEmail) // ★ 이메일 추가
                .phone(initialPhone) // ★ 랜덤 전화번호 추가
                .startDate(LocalDate.now()) // ★ 입사일 추가
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
                .build();

        return userRepository.save(user);
    }
}