package com.bizmate.groupware.board.repository;

import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b WHERE b.isDeleted = false ORDER BY " +
            "CASE WHEN b.boardType = 'NOTICE' THEN 0 ELSE 1 END, b.boardNo DESC")
    List<Board> findAllOrderByPriority();

    List<Board> findByBoardTypeAndIsDeletedFalse(BoardType boardType);

    @Query("SELECT b FROM Board b WHERE b.isDeleted = false")
    List<Board> findAllActive();

    @Query("SELECT b FROM Board b WHERE b.isDeleted = false " +
            "AND (:keyword IS NULL OR b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
    Page<Board> findActiveByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE (:keyword IS NULL OR b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
    Page<Board> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
