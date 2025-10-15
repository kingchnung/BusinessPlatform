package com.bizmate.project.domain;

import com.bizmate.project.domain.enums.task.TaskPriority;
import com.bizmate.project.domain.enums.task.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "assign")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "assign_generator",
        sequenceName = "assign_seq",
        initialValue = 1,
        allocationSize = 1)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "assign_generator")
    private Long taskId;

    @Column(nullable = false)
    private String taskName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @Column
    private LocalDateTime taskStartDate;

    @Column
    private LocalDateTime taskEndDate;

    @PrePersist
    public void PrePersist() {
        if(status == null){
            status = TaskStatus.BEFORE_START;
        }
        if(priority == null){
            priority = TaskPriority.LOW;
        }
    }


}
