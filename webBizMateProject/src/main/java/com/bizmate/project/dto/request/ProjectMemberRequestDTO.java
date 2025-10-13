package com.bizmate.project.dto.request;


import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMemberRequestDTO {

    private Long projectId;

    private Long userId;

    private String pmRoleName;

    private Long taskId;


}
