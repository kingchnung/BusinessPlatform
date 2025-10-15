package com.bizmate.groupware.board.api;

import com.bizmate.groupware.board.dto.CommentDto;
import com.bizmate.groupware.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    // ✅ 댓글 등록
    @PostMapping("/{boardId}")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long boardId,
            @RequestBody CommentDto dto,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        CommentDto saved = commentService.addComment(boardId, dto.getContent(), user);
        return ResponseEntity.ok(saved);
    }

    // ✅ 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        commentService.deleteComment(id, user);
        return ResponseEntity.noContent().build();
    }
}
