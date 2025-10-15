package com.bizmate.groupware.board.service;

import com.bizmate.groupware.board.dto.CommentDto;
import com.bizmate.hr.security.UserPrincipal;

public interface CommentService {

    CommentDto addComment(Long boardId, String content, UserPrincipal user);

    void deleteComment(Long id, UserPrincipal user);
}
