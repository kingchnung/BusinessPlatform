package com.bizmate.project.domain.enums;

public enum ProjectBoardStatus {
    FILE_BOARD("파일게시판"),
    ISSUE_BOARD("이슈게시판");

    private final String status;

    ProjectBoardStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return this.status;
    }
}
