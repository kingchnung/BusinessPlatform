package com.bizmate.project.service;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectBudgetItem;
import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.ProjectTask;
import com.bizmate.project.domain.enums.project.ProjectStatus;

import com.bizmate.project.domain.enums.task.TaskStatus;
import com.bizmate.project.dto.budgetitem.ProjectBudgetItemDTO;
import com.bizmate.project.dto.project.ProjectCreateRequest;
import com.bizmate.project.dto.project.ProjectDetailResponse;
import com.bizmate.project.dto.projectmember.ProjectMemberDTO;
import com.bizmate.project.dto.project.ProjectRequestDTO;
import com.bizmate.project.dto.task.ProjectTaskDTO;
import com.bizmate.project.dto.task.ProjectTaskRequest;
import com.bizmate.project.repository.ProjectBudgetItemRepository;
import com.bizmate.project.repository.ProjectMemberRepository;
import com.bizmate.project.repository.ProjectRepository;
import com.bizmate.project.repository.ProjectTaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
                Employee emp = employeeRepository.findById(pDto.getEmployeeId())
                        .orElseThrow(() -> new IllegalArgumentException("참여자를 찾을 수 없습니다. ID: " + pDto.getEmployeeId()));

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
                if (tDto.getAssigneeId() != null) {
                    // Map에서 직원 ID를 키로 하여 ProjectMember 엔티티를 찾습니다.
                    assignee = participantMemberMap.get(tDto.getAssigneeId());

                    // Map에 해당 직원이 없으면, 프로젝트 참여자가 아니라는 의미
                    if (assignee == null) {
                        throw new IllegalArgumentException(
                                "Task '" + tDto.getTaskName() + "'의 담당자(ID:" + tDto.getAssigneeId() + ")는 " +
                                        "프로젝트 참여 멤버가 아닙니다. 기안 문서를 확인해주세요."
                        );
                    }
                }

                project.addTask(ProjectTask.builder()
                        .taskName(tDto.getTaskName())
                        .taskDescription(tDto.getTaskDescription())
                        .startDate(tDto.getStartDate())
                        .endDate(tDto.getEndDate())
                        .assigneeId(assignee) // 검증된 담당자 또는 null
                        .progressRate(tDto.getProgressRate())
                        .status(TaskStatus.PLANNED)
                        .build());
            }
        }

        Project saved = projectRepository.save(project);
        log.info("✅ 프로젝트 생성 완료 (ID: {})", saved.getProjectId());
        return saved;
    }





}
