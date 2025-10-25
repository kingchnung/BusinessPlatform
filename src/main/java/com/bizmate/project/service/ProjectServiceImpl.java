package com.bizmate.project.service;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.hr.domain.Department;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.repository.DepartmentRepository;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.hr.repository.UserRepository;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectBudgetItem;
import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.ProjectTask;
import com.bizmate.project.domain.enums.project.ProjectStatus;

import com.bizmate.project.domain.enums.task.TaskStatus;
import com.bizmate.project.dto.budgetitem.ProjectBudgetItemDTO;
import com.bizmate.project.dto.project.ProjectDetailResponseDTO;
import com.bizmate.project.dto.project.ProjectRequestDTO;
import com.bizmate.project.dto.projectmember.ProjectMemberDTO;
import com.bizmate.project.dto.task.ProjectTaskDTO;
import com.bizmate.project.repository.ProjectBudgetItemRepository;
import com.bizmate.project.repository.ProjectMemberRepository;
import com.bizmate.project.repository.ProjectRepository;
import com.bizmate.project.repository.ProjectTaskRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
//DB 작업을 원자적으로 처리하도록 도와주는 스프링 어노테이션
//정상 → 커밋, 예외 → 롤백 자동 처리
//Service 계층에서 주로 사용
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectBudgetItemRepository budgetItemRepository;
    private final ProjectTaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;


    /** ✅ 프로젝트 생성 (전자결재 승인 시 자동 호출) */
    @Transactional
    @Override
    public Project createProjectByApproval(ProjectRequestDTO dto, ApprovalDocuments document) {
        log.info("🚀 [프로젝트 자동 생성] 문서ID={}, 프로젝트명={}", document.getDocId(), dto.getProjectName());

        Project project = Project.builder()
                .projectName(dto.getProjectName())
                .projectGoal(dto.getProjectGoal())
                .projectOverview(dto.getProjectOverview())
                .expectedEffect(dto.getExpectedEffect())
                .totalBudget(dto.getTotalBudget())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .approvalDocument(document)
                .department(document.getDepartment())
                .author(document.getAuthorUser())
                .status(ProjectStatus.PLANNING)
                .build();

        // 🔹 참여자 처리 및 Task 담당자 조회를 위한 Map 생성
        // Key: Employee ID, Value: ProjectMember Entity
        Map<Long, ProjectMember> participantMemberMap = new HashMap<>();

        if (dto.getParticipants() != null) {
            for (ProjectMemberDTO pDto : dto.getParticipants()) {
                Employee emp = employeeRepository.findById(pDto.getEmpId())
                        .orElseThrow(() -> new IllegalArgumentException("참여자를 찾을 수 없습니다. ID: " + pDto.getEmpId()));

                ProjectMember newMember = ProjectMember.builder()
                        .employee(emp)
                        .projectRole(pDto.getProjectRole())
                        .build();

                project.addParticipant(newMember); // Project에 멤버 추가 (연관관계 설정)
                participantMemberMap.put(emp.getEmpId(), newMember); // Map에 저장하여 Task에서 재사용
            }
        }


        // 🔹 예산 항목
        if (dto.getBudgetItems() != null) {
            for (ProjectBudgetItemDTO bDto : dto.getBudgetItems()) {
                project.addBudgetItem(ProjectBudgetItem.builder()
                        .itemName(bDto.getItemName())
                        .amount(bDto.getAmount())
                        .build());
            }
        }

        // 🔹 일정(Task) - ProjectMember와 연결하도록 수정됨
        if (dto.getTasks() != null) {
            for (ProjectTaskDTO tDto : dto.getTasks()) {
                ProjectMember assignee = null; // 담당자는 이제 ProjectMember 타입
                if (tDto.getAssignee() != null) {
                    // Map에서 직원 ID를 키로 하여 ProjectMember 엔티티를 찾습니다.
                    assignee = participantMemberMap.get(tDto.getAssignee());

                    // Map에 해당 직원이 없으면, 프로젝트 참여자가 아니라는 의미
                    if (assignee == null) {
                        throw new IllegalArgumentException(
                                "Task '" + tDto.getTaskName() + "'의 담당자(ID:" + tDto.getAssignee() + ")는 " +
                                        "프로젝트 참여 멤버가 아닙니다. 기안 문서를 확인해주세요."
                        );
                    }
                }

                project.addTask(ProjectTask.builder()
                        .taskName(tDto.getTaskName())
                        .taskDescription(tDto.getTaskDescription())
                        .startDate(tDto.getStartDate())
                        .endDate(tDto.getEndDate())
                        .assignee(assignee) // 검증된 담당자 또는 null
                        .progressRate(tDto.getProgressRate())
                        .status(TaskStatus.PLANNED)
                        .build());
            }
        }

        Project saved = projectRepository.save(project);
        log.info("✅ 프로젝트 생성 완료 (ID: {})", saved.getProjectId());
        return saved;
    }

    @Override
    @Transactional
    public ProjectDetailResponseDTO createProject(ProjectRequestDTO dto) {
        UserEntity author = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("작성자 없음"));
        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("부서 없음"));

        Project project = dto.toEntity();
        project.setAuthor(author);
        project.setDepartment(dept);
        projectRepository.save(project);
        return new ProjectDetailResponseDTO(project);
    }

    // ✅ 2. 상세조회
    @Transactional
    public ProjectDetailResponseDTO getProject(Long id) {
        Project project = projectRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트를 찾을 수 없습니다."));
        return new ProjectDetailResponseDTO(project);
    }

    // ✅ 3. 일반 유저용 목록 조회 (종료되지 않은 프로젝트만)
    public List<ProjectDetailResponseDTO> getActiveProjects() {
        return projectRepository.findActiveProjects().stream()
                .map(ProjectDetailResponseDTO::new)
                .toList();
    }

    // ✅ 4. 관리자용 목록 조회 (모든 프로젝트)
    public List<ProjectDetailResponseDTO> getAllProjectsForAdmin() {
        return projectRepository.findAllForAdmin().stream()
                .map(ProjectDetailResponseDTO::new)
                .toList();
    }
    //상태값변경
    @Override
    @Transactional
    public ProjectDetailResponseDTO updateProjectStatus(Long projectId, ProjectStatus status) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 없음"));
        project.setStatus(status);
        return new ProjectDetailResponseDTO(project);
    }

    // ✅ 5. 논리삭제 (endDate 갱신)
    @Override
    @Transactional
    public void closeProject(Long projectId) {
        int updated = projectRepository.updateEndDate(projectId, LocalDate.now());
        if (updated == 0) {
            throw new EntityNotFoundException("프로젝트를 찾을 수 없습니다.");
        }
    }







}
