package com.bizmate.groupware.board.dto;

import com.bizmate.groupware.board.domain.BoardType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private Long boardNo;
    private BoardType type;
    private String title;
    private String content;
    private String authorName; // 표시용 (익명 처리 포함)
    private LocalDateTime createdAt;
    private Boolean isDeleted;
}
