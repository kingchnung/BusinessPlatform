package com.bizmate.project.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;


import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProjectRequestDTO {

    // 프로젝트 번호
    @NotBlank(message = "프로젝트 번호가 기입되지 않았습니다.")
    private String projectNo;

    // 프로젝트 명
    @NotBlank(message = "프로젝트 이름을 입력 해주세요")
    private String projectName;

    // 프로젝트 시작일
    @NotBlank(message = "프로젝트 시작일이 지정되지 않았습니다.")
    private LocalDateTime projectStartDate;

    // 프로젝트 마감일
    private LocalDateTime projectEndDate;

    // 프로젝트 진행상태
    @NotBlank(message = "프로젝트 진행상태를 기입해주세요.")
    private String projectStatus;

    // 프로젝트 중요도
    private String projectImportance;

    // 프로젝트 관리자
    @NotBlank(message = "프로젝트 관리자를 등록 해주세요")
    private Long userId;

    // 프로젝트와 연결된 거래처
    @NotBlank(message = "거래처를 등록 해주세요")
    private Long clientId;

    // 책임자 명
    private String managerName;

    //프로젝트 예산
    private Long projectBudget;

}
