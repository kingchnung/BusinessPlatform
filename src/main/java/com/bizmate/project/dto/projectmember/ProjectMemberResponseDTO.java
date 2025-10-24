package com.bizmate.project.dto.projectmember;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProjectMemberResponseDTO {

    private Long projectMemberId;
    private String employeeName;
    private String departmentName;
    private String projectName;
    private String projectRole;

}
