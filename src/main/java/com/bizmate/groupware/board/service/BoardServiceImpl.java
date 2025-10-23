package com.bizmate.groupware.board.service;

import com.bizmate.common.exception.VerificationFailedException;
import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.BoardType;
import com.bizmate.groupware.board.domain.Comment;
import com.bizmate.groupware.board.dto.BoardDto;
import com.bizmate.groupware.board.repository.BoardRepository;
import com.bizmate.groupware.board.repository.CommentRepository;
import com.bizmate.hr.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    //게시글 등록
    @Override
    @Transactional
    public BoardDto createBoard(BoardDto dto, UserPrincipal user) {
        if (!user.isAdmin() && dto.getBoardType().name().equals(BoardType.NOTICE.name())) {
            throw new AccessDeniedException("공지사항은 관리자만 작성할 수 있습니다.");
        }

        String displayName = (dto.getBoardType() == BoardType.SUGGESTION)
                ? "익명" : user.getEmpName();

        Board board = new Board(); // ✅ Builder 대신 new 사용
        board.setBoardType(dto.getBoardType());
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setAuthorId(user.getUsername());
        board.setAuthorName(displayName);
        board.setDeleted(false);


        board.markCreated(user);
        Board saved = boardRepository.saveAndFlush(board);
        return BoardDto.fromEntity(saved);
    }

    //게시글 삭제
    @Override
    @Transactional
    public void deleteBoard(Long boardNo, UserPrincipal currentUser) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        // 일반 사용자는 본인 글만 논리삭제 가능
        if (!currentUser.isAdmin()) {
            if (!board.getAuthorId().equals(currentUser.getUsername())) {
                throw new AccessDeniedException("본인 글만 삭제 가능합니다.");
            }
            board.setDeleted(true);
            return;
        }

        // 관리자면: 논리삭제 시 isDeleted=true면 물리삭제
        if (board.isDeleted()) {
            boardRepository.delete(board);  // 물리삭제
        } else {
            board.markUpdated(currentUser);
            board.setDeleted(true);         // 논리삭제 1단계
        }
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

        comment.markCreated(user);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public List<BoardDto> getBoardsByType(BoardType type) {
        return boardRepository.findByBoardTypeAndIsDeletedFalse(type)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public BoardDto getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        return toDto(board);
    }

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
        board.markUpdated(user);
        return toDto(board);
    }

    @Override
    @Transactional
    public PageResponseDTO<BoardDto> getBoardPage(PageRequestDTO pageRequestDTO, UserPrincipal user) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Board> result = boardRepository.findActiveByKeyword(pageRequestDTO.getKeyword(), pageable);

        List<BoardDto> dtoList = result.getContent().stream()
                .map(board -> toDtoForUser(board, user))
                .toList();

        return PageResponseDTO.<BoardDto>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(result.getTotalElements())
                .build();
    }


    @Override
    @Transactional
    public PageResponseDTO<BoardDto> getBoardPageForAdmin(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Board> result = boardRepository.findAllByKeyword(pageRequestDTO.getKeyword(), pageable);

        List<BoardDto> dtoList = result.getContent().stream()
                .map(BoardDto::fromEntity)
                .toList();

        return PageResponseDTO.<BoardDto>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(result.getTotalElements())
                .build();
    }

    private BoardDto toDto(Board board) {
        return BoardDto.builder()
                .boardNo(board.getBoardNo())
                .boardType(board.getBoardType())
                .title(board.getTitle())
                .content(board.getContent())
                .authorName(board.getAuthorName())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .isDeleted(board.isDeleted())
                .build();
    }

    private BoardDto toDtoForUser(Board board, UserPrincipal user) {
        String displayName = board.getBoardType() == BoardType.SUGGESTION
                ? (user.isAdmin() ? board.getAuthorName() : "익명") // 관리자면 실명
                : board.getAuthorName();

        return BoardDto.builder()
                .boardNo(board.getBoardNo())
                .title(board.getTitle())
                .content(board.getContent())
                .boardType(board.getBoardType())
                .authorName(displayName)
                .isDeleted(board.isDeleted())
                .build();
    }
}
