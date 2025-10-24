package com.bizmate.project.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMemberRequestDTO {

    @NotBlank(message = "프로젝트가 정상적으로 기입되지 않았습니다.")
    private Long projectId;

    @NotBlank(message = "사용자가 정상적으로 기입되지 않았습니다.")
    private Long userId;

    @NotBlank(message = "역할이 제대로 부여되지 않았습니다.")
    private String pmRoleName;


    private Long taskId;


}
