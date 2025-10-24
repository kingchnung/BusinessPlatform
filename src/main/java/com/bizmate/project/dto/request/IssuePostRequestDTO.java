package com.bizmate.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class IssuePostRequestDTO {

    @NotBlank(message = "사용자가 입력되지 않았습니다.")
    private Long userId;

    @NotBlank(message = "제목이 입력되지 않았습니다.")
    private String ipTitle;

    @NotBlank(message = "본문이 입력되지 않았습니다.")
    private String ipContent;

    @NotBlank(message = "게시판을 지정하지 않았습니다.")
    private Long projectBoardId;

    @NotBlank(message = "프로젝트가 지정되지 않았습니다.")
    private Long projectId;

}
