package com.bizmate.project.domain.enums;

import lombok.Getter;

@Getter
public enum AssignStatus {
    BEFORE_START("업무 시작 전"),
    IN_PROGRESS("업무 진행 중"),
    COMPLETED("업무 완료");


    private final String status;


    AssignStatus(String status){
        this.status = status;
    }


}
