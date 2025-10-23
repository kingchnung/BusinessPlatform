package com.bizmate.project.dto.projectmember;


import com.bizmate.hr.domain.Employee;
import com.bizmate.project.domain.Project;
import com.bizmate.project.domain.ProjectMember;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberDTO {

    private Long projectMemberId;
    private Long projectId;
    private Long employeeId;
    private String employeeName;
    private String projectRole;

    // ✅ Entity → DTO
    public static ProjectMemberDTO fromEntity(ProjectMember entity) {
        return ProjectMemberDTO.builder()
                .projectMemberId(entity.getProjectMemberId())
                .projectId(entity.getProject() != null ? entity.getProject().getProjectId() : null)
                .employeeId(entity.getEmployee() != null ? entity.getEmployee().getEmpId() : null)
                .employeeName(entity.getEmployee() != null ? entity.getEmployee().getEmpName() : null)
                .projectRole(entity.getProjectRole())
                .build();
    }

    // ✅ DTO → Entity
    public ProjectMember toEntity(Project project, Employee employee) {
        return ProjectMember.builder()
                .projectMemberId(this.projectMemberId)
                .project(project)
                .employee(employee)
                .projectRole(this.projectRole)
                .build();
    }
}
