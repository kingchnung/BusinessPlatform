package com.bizmate.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskDTO {
    private String taskName;
    private String taskDescription;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long assigneeId;
    private int progressRate;
}