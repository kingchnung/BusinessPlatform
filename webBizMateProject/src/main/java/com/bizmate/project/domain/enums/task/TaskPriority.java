package com.bizmate.project.domain.enums.task;

import lombok.Getter;

@Getter
public enum TaskPriority {
    HIGH("최우선"),
    MEDIUM("중요"),
    LOW("보통");

    private final String priority;

    TaskPriority(String priority) {
        this.priority = priority;
    }
}
