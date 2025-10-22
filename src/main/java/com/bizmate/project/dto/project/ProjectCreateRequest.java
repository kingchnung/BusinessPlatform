package com.bizmate.project.dto.project;

import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.enums.project.ProjectStatus;
import com.bizmate.project.dto.budgetitem.ProjectBudgetItemDTO;
import com.bizmate.project.dto.projectmember.ProjectMemberDTO;
import com.bizmate.project.dto.task.ProjectTaskDTO;

import java.time.LocalDate;
import java.util.List;

public class ProjectCreateRequest {
    // --- 기본 프로젝트 정보 ---
    private String projectName;
    private String projectGoal;
    private String projectOverview;
    private String expectedEffect;
    private Long totalBudget;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long authorId;      // 프로젝트 최초 생성자(직원 ID)
    private Long departmentId;  // 프로젝트 담당 부서 ID

    // --- 연관 정보 ---
    private List<ProjectMemberDTO> participants;
    private List<ProjectBudgetItemDTO> budgetItems;
    private List<ProjectTaskDTO> tasks;

    /**
     * DTO를 Project 엔티티로 변환하는 유틸리티 메서드입니다.
     * @return Project 엔티티
     */
    public Project toEntity() {
        return Project.builder()
                .projectName(this.projectName)
                .projectGoal(this.projectGoal)
                .projectOverview(this.projectOverview)
                .expectedEffect(this.expectedEffect)
                .totalBudget(this.totalBudget)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .status(ProjectStatus.PLANNING) // 생성 시 기본 상태는 '계획중'
                .build();
    }
}
