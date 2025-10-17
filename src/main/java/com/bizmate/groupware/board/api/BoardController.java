package com.bizmate.groupware.board.api;

import com.bizmate.groupware.board.domain.BoardType;
import com.bizmate.groupware.board.dto.BoardDto;
import com.bizmate.groupware.board.service.BoardService;
import com.bizmate.hr.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    // ✅ 게시판 목록 조회 (공지사항, 건의사항, 일반)
    @GetMapping
    public ResponseEntity<List<BoardDto>> getBoards(@RequestParam(required = false) BoardType type) {
        List<BoardDto> boards = (type != null)
                ? boardService.getBoardsByType(type)
                : boardService.getAllBoards(); // ✅ 전체 목록용 서비스 추가
        return ResponseEntity.ok(boards);
    }

    // ✅ 게시글 상세 조회
    @GetMapping("/{boardNo}")
    public ResponseEntity<BoardDto> getBoard(@PathVariable Long boardNo) {
        BoardDto board = boardService.getBoard(boardNo);
        return ResponseEntity.ok(board);
    }

    // ✅ 게시글 등록
    @PostMapping
    public ResponseEntity<BoardDto> createBoard(
            @RequestBody BoardDto dto,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        BoardDto saved = boardService.createBoard(dto, user);
        return ResponseEntity.ok(saved);
    }

    // ✅ 게시글 수정
    @PutMapping("/{boardNo}")
    public ResponseEntity<BoardDto> updateBoard(
            @PathVariable Long boardNo,
            @RequestBody BoardDto dto,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        BoardDto updated = boardService.updateBoard(boardNo, dto, user);
        return ResponseEntity.ok(updated);
    }

    // ✅ 게시글 삭제 (논리 삭제)
    @DeleteMapping("/{boardNo}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long boardNo,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        boardService.deleteBoard(boardNo, user);
        return ResponseEntity.noContent().build();
    }

    // 공지사항 등록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/notice")
    public ResponseEntity<BoardDto> createNotice(@RequestBody BoardDto dto, @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(boardService.createBoard(dto, user));
    }
}
