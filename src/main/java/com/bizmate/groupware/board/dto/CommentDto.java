package com.bizmate.groupware.board.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long boardId;
    private String content;
    private String authorName; // 표시용 (익명 처리 포함)
    private LocalDateTime createdAt;
}
