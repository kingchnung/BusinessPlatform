package com.bizmate.project.dto.request;


import lombok.*;


import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProjectRequestDTO {

    private String projectName;
    private String projectGoal;
    private String projectOverview;
    private String expectedEffect;
    private Long totalBudget;
    private LocalDate startDate;
    private LocalDate endDate;

    private List<ProjectMemberDTO> participants;
    private List<ProjectBudgetItemDTO> budgetItems;
    private List<ProjectTaskDTO> tasks;

}
