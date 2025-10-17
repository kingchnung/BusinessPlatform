package com.bizmate.groupware.board.service;

import com.bizmate.groupware.board.domain.Board;
import com.bizmate.groupware.board.domain.Comment;
import com.bizmate.groupware.board.dto.CommentDto;
import com.bizmate.groupware.board.repository.BoardRepository;
import com.bizmate.groupware.board.repository.CommentRepository;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final CommentHistoryService commentHistoryService;

    /**
     * ✅ 댓글 등록
     */
    @Override
    public CommentDto addComment(Long boardNo, String content, UserPrincipal user) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        // 익명 게시판 여부 체크
        boolean isAnonymous = board.getBoardType().name().equalsIgnoreCase("SUGGESTION");

        UserDTO userDTO = new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmpName(),
                user.getEmail()
        );

        Comment comment = Comment.builder()
                .board(board)
                .content(content)
                .authorId(user.getUsername())
                .authorName(isAnonymous ? "익명" : user.getEmpName())
                .anonymous(isAnonymous)
                .isDeleted(false)
                .build();

        comment.markCreated(userDTO);

        Comment saved = commentRepository.save(comment);
        commentHistoryService.saveHistory(saved, user, "등록", "댓글 작성");
        log.info("💬 댓글 등록 완료: {}", saved.getContent());

        return CommentDto.fromEntity(saved);
    }

    /**
     * ✅ 댓글 수정
     */
    @Override
    public CommentDto editComment(Long commentId, String newContent, UserPrincipal user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 작성자 본인 or 관리자만 수정 가능
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!user.getUsername().equals(comment.getAuthorId()) && !isAdmin) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        // ✅ 내용 수정
        comment.setContent(newContent);

        // ✅ UserPrincipal → UserDTO 변환
        UserDTO userDTO = new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmpName(),
                user.getEmail()
        );

        // ✅ 수정자 정보 기록
        comment.markUpdated(userDTO);

        Comment updated = commentRepository.save(comment);

        // ✅ 수정 이력 기록
        commentHistoryService.saveHistory(updated, user, "수정", "댓글 수정");

        log.info("✏️ 댓글 수정 완료: {}", updated.getCommentNo());
        return CommentDto.fromEntity(updated);
    }

    /**
     * ✅ 댓글 삭제
     */
    @Override
    public void deleteComment(Long id, UserPrincipal user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 삭제 권한 체크 (본인 또는 관리자만)
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!user.getUsername().equals(comment.getAuthorId()) && !isAdmin) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        comment.setDeleted(true);

        UserDTO userDTO = new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmpName(),
                user.getEmail()
        );

        comment.markUpdated(userDTO);
        commentRepository.save(comment);
        commentHistoryService.saveHistory(comment, user, "삭제", "댓글 삭제");
        log.info("🗑️ 댓글 논리삭제 완료: {}", id);
    }

    /**
     * ✅ 댓글 목록 조회
     */
    public List<CommentDto> getComments(Long boardNo) {
        return commentRepository.findByBoard_BoardNoAndIsDeletedFalse(boardNo)
                .stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
    }
}
