package com.bizmate.project.domain.enums;

import lombok.Getter;

@Getter
public enum ProjectStatus {
    BEFORE_START("시작 전"),
    IN_PROGRESS("진행 중"),
    COMPLETED("완료");

    private final String status;

    ProjectStatus(String status){
        this.status = status;
    }


    // 열거형에 한글 설정시 생성자로 불러와야 한다
    // 한글 미 설정시엔 생성자 부를필요 없다.

}
