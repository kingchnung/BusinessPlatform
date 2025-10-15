package com.bizmate.groupware.board.service;

import com.bizmate.groupware.board.dto.CommentDto;
import com.bizmate.hr.security.UserPrincipal;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService{
    @Override
    public CommentDto addComment(Long boardId, String content, UserPrincipal user) {
        return null;
    }

    @Override
    public void deleteComment(Long id, UserPrincipal user) {

    }
}
