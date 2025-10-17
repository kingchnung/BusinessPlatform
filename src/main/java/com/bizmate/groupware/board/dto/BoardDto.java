package com.bizmate.groupware.board.dto;

import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.BoardType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private Long boardNo;
    private BoardType boardType;
    private String title;
    private String content;
    private String authorId;
    private String authorName; // 표시용 (익명 처리 포함)
    private boolean isDeleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<CommentDto> comments;

    public static BoardDto fromEntity(Board entity) {
        return BoardDto.builder()
                .boardNo(entity.getBoardNo())
                .boardType(entity.getBoardType())
                .title(entity.getTitle())
                .content(entity.getContent())
                .authorName(
                        entity.getBoardType() == BoardType.SUGGESTION
                                ? "익명"
                                : entity.getAuthorName()
                )
                .authorId(entity.getAuthorId())
                .isDeleted(entity.isDeleted())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .comments(entity.getComments() != null
                        ? entity.getComments().stream()
                        .filter(c -> !c.isDeleted()) // 논리삭제 제외
                        .map(CommentDto::fromEntity)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }

    public Board toEntity() {
        return Board.builder()
                .boardNo(this.boardNo)
                .boardType(this.boardType)
                .title(this.title)
                .content(this.content)
                .authorName(this.authorName)
                .authorId(this.authorId)
                .isDeleted(this.isDeleted)
                .build();
    }
}
