package com.bizmate.groupware.board.service;

import com.bizmate.groupware.board.dto.CommentDto;
import com.bizmate.hr.security.UserPrincipal;

import java.util.List;

public interface CommentService {

    List<CommentDto> getComments(Long boardNo);
    CommentDto addComment(Long boardNo, String content, UserPrincipal user);

    void deleteComment(Long commentNo, UserPrincipal user);
}
