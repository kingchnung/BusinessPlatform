package com.bizmate.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequestDTO {

    @NotBlank(message = "업무명을 입력해주세요")
    private String taskName;

    @NotBlank(message = "업무 상태를 입력해주세요")
    private String taskStatus;

    @NotBlank(message = "업무 중요도를 입력해주세요")
    private String taskPriority;

    private LocalDateTime taskStartDate;

    private LocalDateTime taskEndDate;

}
