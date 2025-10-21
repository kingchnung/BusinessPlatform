package com.bizmate.project.domain;

import com.bizmate.hr.domain.Employee;
import com.bizmate.project.domain.enums.task.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "PROJECT_TASKS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;

    @Column(nullable = false, length = 150)
    private String taskName;

    @Column(length = 500)
    private String taskDescription;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TaskStatus status = TaskStatus.PLANNED;

    private int progressRate; // 0~100%

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNEE_ID")
    private Employee assignee; // 담당자
}
