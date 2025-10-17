package com.bizmate.groupware.board.service;

import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.BoardType;
import com.bizmate.groupware.board.domain.Comment;
import com.bizmate.groupware.board.dto.BoardDto;
import com.bizmate.groupware.board.repository.BoardHistoryRepository;
import com.bizmate.groupware.board.repository.BoardRepository;
import com.bizmate.groupware.board.repository.CommentRepository;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final BoardHistoryService boardHistoryService;

    //게시글 등록
    @Override
    @Transactional
    public BoardDto createBoard(BoardDto dto, UserPrincipal user) {
//        if (dto.getBoardType() == BoardType.NOTICE && !user.isAdmin()) {
//            throw new VerificationFailedException("공지사항은 관리자만 등록할 수 있습니다.");
//        }

        String displayName = (dto.getBoardType() == BoardType.SUGGESTION)
                ? "익명" : user.getEmpName();

        Board board = Board.builder()
                .boardType(dto.getBoardType())
                .title(dto.getTitle())
                .content(dto.getContent())
                .authorId(user.getUsername())
                .authorName(displayName)
                .isDeleted(false)
                .build();

        UserDTO userDTO = new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmpName(),
                user.getEmail()
        );

        board.markCreated(userDTO);

        Board saved = boardRepository.save(board);
        boardHistoryService.saveHistory(saved, user, "등록", "게시글 생성");
        return toDto(boardRepository.save(board));
    }

    //게시글 수정
    @Override
    @Transactional
    public BoardDto updateBoard(Long id, BoardDto dto, UserPrincipal user) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

//        if (!user.isAdmin() && !board.getAuthorId().equals(user.getUserId())) {
//            throw new VerificationFailedException("수정 권한이 없습니다.");
//        }

        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());

        UserDTO userDTO = new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmpName(),
                user.getEmail()
        );

        board.markUpdated(userDTO);

        Board updated = boardRepository.save(board);
        boardHistoryService.saveHistory(updated, user, "수정", "게시글 수정");
        return toDto(updated);
    }

    //게시글 삭제
    @Override
    @Transactional
    public void deleteBoard(Long boardId, UserPrincipal user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        board.setDeleted(true);
        boardRepository.save(board);

        // 논리 삭제
//        if (!user.isAdmin() && !board.getAuthorName().equals(user.getUserId())) {
//            throw new VerificationFailedException("삭제 권한이 없습니다.");
//        }

        UserDTO userDTO = new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmpName(),
                user.getEmail()
        );

        board.markUpdated(userDTO);
        boardRepository.save(board);
        boardHistoryService.saveHistory(board, user, "삭제", "게시글 삭제");
    }

    //댓글 등록
    @Transactional
    @Override
    public Comment addComment(Long boardId, String content, UserPrincipal user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        // 공지사항은 댓글 금지
        if (board.getBoardType() == BoardType.NOTICE) {
            throw new VerificationFailedException("공지사항에는 댓글을 달 수 없습니다.");
        }

        String displayName = board.getBoardType() == BoardType.SUGGESTION ? "익명" : user.getEmpName();

        Comment comment = Comment.builder()
                .board(board)
                .content(content)
                .authorId(user.getUsername())
                .authorName(displayName)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public List<BoardDto> getBoardsByType(BoardType type) {
        return boardRepository.findByBoardTypeAndIsDeletedFalse(type)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public BoardDto getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        return toDto(board);
    }

    @Override
    public List<BoardDto> getAllBoards() {
        return boardRepository.findAllOrderByPriority()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private BoardDto toDto(Board board) {
        return BoardDto.builder()
                .boardNo(board.getBoardNo())
                .boardType(board.getBoardType())
                .title(board.getTitle())
                .content(board.getContent())
                .authorName(board.getAuthorName())
                .createdAt(board.getCreatedAt())
                .isDeleted(board.isDeleted())
                .build();
    }

}
