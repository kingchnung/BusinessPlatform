package com.bizmate.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TaskResponseDTO {

    private String taskName;

    private String taskPriority;

    private String taskStatus;

    private LocalDateTime taskStartDate;

    private LocalDateTime taskEndDate;
}
