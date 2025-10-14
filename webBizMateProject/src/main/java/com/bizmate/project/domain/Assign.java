package com.bizmate.project.domain;

import com.bizmate.project.domain.enums.AssignStatus;
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
public class Assign {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "assign_generator")
    private Long taskId;

    @Column(nullable = false)
    private String taskName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AssignStatus taskPriority = AssignStatus.BEFORE_START;



    @Column
    private LocalDateTime taskStartDate;

    @Column
    private LocalDateTime taskEndDate;

    @PrePersist
    public void PrePersist() {

    }


}
