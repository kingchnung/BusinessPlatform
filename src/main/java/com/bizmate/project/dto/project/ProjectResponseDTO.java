package com.bizmate.project.dto.project;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProjectResponseDTO {

    private Long projectId;

    private String projectNo;

    private String projectName;

    private LocalDateTime projectStartDate;

    private LocalDateTime projectEndDate;

    private String projectStatus;

    private String projectImportance;

    private String projectManager;

    private String clientCeo;

    private String clientCompany;

    private String clientEmail;

    private String clientContact;

}
