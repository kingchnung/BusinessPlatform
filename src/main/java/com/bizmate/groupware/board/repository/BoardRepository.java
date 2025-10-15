package com.bizmate.groupware.board.repository;

import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b WHERE b.isDeleted = false")
    List<Board> boardList();

    List<Board> findByTypeAndIsDeletedFalseOrderByCreatedAtDesc(BoardType type);
}
