package com.bizmate.project.dto.request;

import com.bizmate.project.domain.enums.ProjectImportance;
import com.bizmate.project.domain.enums.ProjectStatus;
import com.bizmate.project.domain.hr.Users;
import com.bizmate.project.domain.sails.Client;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProjectRequestDTO {

    private String projectNo;

    private String projectName;

    private LocalDateTime projectStartDate;

    private LocalDateTime projectEndDate;

    private String projectStatus;

    private String projectImportance;

    private Long userId;

    private Long clientId;

}
