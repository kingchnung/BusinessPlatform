package com.bizmate.project.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProjectMemberResponseDTO {

    private String pmRoleName;

    private String pmName;

    private String pmEmail;

    private String pmPhone;

    private String gradeName;

}
