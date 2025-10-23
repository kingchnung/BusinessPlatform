package com.bizmate.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ProjectBoardRequestDTO {

    @NotBlank( message = "게시판 명을 입력해주세요")
    private String boardTitle;

    @NotBlank( message = "게시판 타입을 입력해주세요")
    private String boardType;


    @NotBlank( message = "프로젝트를 기입 해주세요")
    private Long projectId;

    

}
