package com.bizmate.project.service.impl;

import com.bizmate.groupware.approval.domain.ApprovalDocuments;
import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.repository.EmployeeRepository;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectBudgetItem;
import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.ProjectTask;
import com.bizmate.project.domain.enums.project.ProjectStatus;

import com.bizmate.project.domain.enums.task.TaskStatus;
import com.bizmate.project.dto.request.ProjectBudgetItemDTO;
import com.bizmate.project.dto.request.ProjectMemberDTO;
import com.bizmate.project.dto.request.ProjectRequestDTO;
import com.bizmate.project.dto.request.ProjectTaskDTO;
import com.bizmate.project.repository.ProjectBudgetItemRepository;
import com.bizmate.project.repository.ProjectMemberRepository;
import com.bizmate.project.repository.ProjectRepository;
import com.bizmate.project.repository.ProjectTaskRepository;
import com.bizmate.project.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
//DB 작업을 원자적으로 처리하도록 도와주는 스프링 어노테이션
//정상 → 커밋, 예외 → 롤백 자동 처리
//Service 계층에서 주로 사용
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository participantRepository;
    private final ProjectBudgetItemRepository budgetItemRepository;
    private final ProjectTaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    /** ✅ 프로젝트 생성 (전자결재 승인 시 자동 호출) */
    @Transactional
    @Override
    public Project createProject(ProjectRequestDTO dto, ApprovalDocuments document) {
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

        // 🔹 참여자
        if (dto.getParticipants() != null) {
            for (ProjectMemberDTO pDto : dto.getParticipants()) {
                Employee emp = employeeRepository.findById(pDto.getEmpId()).orElse(null);
                if (emp != null) {
                    project.addParticipant(ProjectMember.builder()
                            .employee(emp)
                            .role(pDto.getRole())
                            .build());
                }
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

        // 🔹 일정(Task)
        if (dto.getTasks() != null) {
            for (ProjectTaskDTO tDto : dto.getTasks()) {
                Employee assignee = tDto.getAssigneeId() != null
                        ? employeeRepository.findById(tDto.getAssigneeId()).orElse(null)
                        : null;

                project.addTask(ProjectTask.builder()
                        .taskName(tDto.getTaskName())
                        .taskDescription(tDto.getTaskDescription())
                        .startDate(tDto.getStartDate())
                        .endDate(tDto.getEndDate())
                        .assignee(assignee)
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
