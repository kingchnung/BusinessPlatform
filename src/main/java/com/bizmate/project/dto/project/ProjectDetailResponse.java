package com.bizmate.project.dto.project;

import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectBudgetItem;
import com.bizmate.project.domain.ProjectMember;
import com.bizmate.project.domain.ProjectTask;
import com.bizmate.project.domain.enums.project.ProjectStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProjectDetailResponse {

    private final Long projectId;
    private final String projectName;
    private final String projectGoal;
    private final String projectOverview;
    private final String expectedEffect;
    private final Long totalBudget;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final ProjectStatus status;
    private final SimpleAuthorDTO author;
    private final SimpleDepartmentDTO department;

    private final List<ProjectMemberResponseDTO> participants;
    private final List<ProjectBudgetItemResponseDTO> budgetItems;
    private final List<ProjectTaskResponseDTO> tasks;

    public ProjectDetailResponse(Project project) {
        this.projectId = project.getProjectId();
        this.projectName = project.getProjectName();
        this.projectGoal = project.getProjectGoal();
        this.projectOverview = project.getProjectOverview();
        this.expectedEffect = project.getExpectedEffect();
        this.totalBudget = project.getTotalBudget();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.status = project.getStatus();
        this.author = new SimpleAuthorDTO(project.getAuthor());
        this.department = new SimpleDepartmentDTO(project.getDepartment());

        this.participants = project.getParticipants().stream()
                .map(ProjectMemberResponseDTO::new)
                .collect(Collectors.toList());

        this.budgetItems = project.getBudgetItems().stream()
                .map(ProjectBudgetItemResponseDTO::new)
                .collect(Collectors.toList());

        this.tasks = project.getTasks().stream()
                .map(ProjectTaskResponseDTO::new)
                .collect(Collectors.toList());
    }

    // --- 내부 응답 DTO 클래스들 ---

    @Getter
    private static class SimpleAuthorDTO {
        private final Long userId;
        private final String name;

        public SimpleAuthorDTO(com.bizmate.hr.domain.UserEntity user) {
            this.userId = user.getUserId();
            this.name = user.getUsername(); // 혹은 Employee의 이름 필드
        }
    }

    @Getter
    private static class SimpleDepartmentDTO {
        private final Long deptId;
        private final String deptName;

        public SimpleDepartmentDTO(com.bizmate.hr.domain.Department department) {
            this.deptId = department.getDeptId();
            this.deptName = department.getDeptName();
        }
    }

    @Getter
    private static class ProjectMemberResponseDTO {
        private final Long projectMemberId;
        private final Long empId;
        private final String empName;
        private final String role;
        // 필요 시 직급, 부서명 등 추가 가능
        // private final String position;
        // private final String departmentName;

        public ProjectMemberResponseDTO(ProjectMember member) {
            this.projectMemberId = member.getProjectMemberId();
            this.role = member.getProjectRole();
            this.empId = member.getEmployee().getEmpId();
            this.empName = member.getEmployee().getEmpName();
        }
    }

    @Getter
    private static class ProjectBudgetItemResponseDTO {
        private final Long itemId;
        private final String itemName;
        private final Long amount;

        public ProjectBudgetItemResponseDTO(ProjectBudgetItem item) {
            this.itemId = item.getItemId();
            this.itemName = item.getItemName();
            this.amount = item.getAmount();
        }
    }

    @Getter
    private static class ProjectTaskResponseDTO {
        private final Long taskId;
        private final String taskName;
        private final String taskDescription;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final String status;
        private final int progressRate;
        private final SimpleAssigneeDTO assignee;

        public ProjectTaskResponseDTO(ProjectTask task) {
            this.taskId = task.getTaskId();
            this.taskName = task.getTaskName();
            this.taskDescription = task.getTaskDescription();
            this.startDate = task.getStartDate();
            this.endDate = task.getEndDate();
            this.status = task.getStatus().name();
            this.progressRate = task.getProgressRate();
            this.assignee = task.getAssigneeId() != null ? new SimpleAssigneeDTO(task.getAssigneeId()) : null;
        }
    }

    @Getter
    private static class SimpleAssigneeDTO {
        private final Long projectMemberId;
        private final Long empId;
        private final String empName;

        public SimpleAssigneeDTO(ProjectMember member) {
            this.projectMemberId = member.getProjectMemberId();
            this.empId = member.getEmployee().getEmpId();
            this.empName = member.getEmployee().getEmpName();
        }
    }

}
