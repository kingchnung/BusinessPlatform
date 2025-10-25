package com.bizmate.project.dto.project;

import com.bizmate.hr.domain.UserEntity;
import lombok.Getter;

@Getter
public class SimpleAuthorDTO {
    private final Long userId;
    private final String username;

    public SimpleAuthorDTO(UserEntity user) {
        this.userId = user.getUserId();
        this.username = user.getUsername(); // 필요 시 employee 이름으로 교체
    }
}
