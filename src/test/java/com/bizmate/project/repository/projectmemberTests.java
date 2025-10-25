package com.bizmate.project.repository;

import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectMember;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SpringBootTest
public class projectmemberTests {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    private final Random random = new Random();

    private final List<String> roles = List.of("PM", "Backend", "Frontend", "Designer", "QA");


    @Test
    @Transactional
    void insertProjectMembers() {

        // ✅ 프로젝트 중 ID 1,5 제외
        List<Project> targetProjects = projectRepository.findAll()
                .stream()
                .filter(p -> p.getProjectId() != 1 && p.getProjectId() != 5)
                .toList();

        for (Project project : targetProjects) {
            Department dept = project.getDepartment();

            // ✅ PM 선정 (부서장 or 팀장)
            Optional<Employee> pmOpt = employeeRepository.findByDepartmentAndPositionCode(dept, "P05");
            if (pmOpt.isEmpty()) {
                System.out.printf("⚠️ %s 부서의 PM(팀장)을 찾을 수 없습니다.%n", dept.getDeptName());
                continue;
            }

            Employee pm = pmOpt.get();
            Set<Employee> selected = new HashSet<>();
            List<ProjectMember> members = new ArrayList<>();

            // ✅ 1. PM 등록
            members.add(ProjectMember.builder()
                    .project(project)
                    .employee(pm)
                    .projectRole("PM")
                    .build());
            selected.add(pm);

            // ✅ 2. 같은 부서 직원들 중 랜덤 선택 (3~4명)
            List<Employee> candidates = employeeRepository.findByDepartment_DeptId(dept.getDeptId());
            Collections.shuffle(candidates);

            int memberCount = random.nextInt(2) + 3; // 3~4명
            for (Employee e : candidates) {
                if (selected.contains(e)) continue;
                if (members.size() >= memberCount) break;

                String role = roles.get(random.nextInt(roles.size() - 1) + 1); // PM 제외
                members.add(ProjectMember.builder()
                        .project(project)
                        .employee(e)
                        .projectRole(role)
                        .build());
                selected.add(e);
            }

            // ✅ 저장
            projectMemberRepository.saveAll(members);

            // ✅ 로그 출력
            System.out.printf("✅ [%s] 프로젝트(%d) - %s 부서 멤버 %d명 등록 완료%n",
                    project.getProjectName(), project.getProjectId(), dept.getDeptName(), members.size());

            members.forEach(m ->
                    System.out.printf("   → %s (%s)%n", m.getEmployee().getEmpName(), m.getProjectRole())
            );
        }
    }
}
