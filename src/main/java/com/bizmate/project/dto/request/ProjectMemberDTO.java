package com.bizmate.project.dto.request;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberDTO {
    private Long empId;
    private String role;
}
