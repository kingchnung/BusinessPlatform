package com.bizmate.groupware.board.service;

import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.BoardType;
import com.bizmate.groupware.board.domain.Comment;
import com.bizmate.groupware.board.dto.BoardDto;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;

public interface BoardService {
    Board createBoard(BoardDto dto, UserPrincipal user);
    void deleteBoard(Long boardId, UserPrincipal user);
    Comment addComment(Long boardId, String content, UserPrincipal user);

    List<BoardDto> getBoardsByType(BoardType type);

    BoardDto getBoard(Long id);

    BoardDto updateBoard(Long id, BoardDto dto, UserPrincipal user);
}
