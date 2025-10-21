package com.bizmate.groupware.board.domain;

import com.bizmate.common.domain.BaseEntity;
import com.bizmate.hr.dto.user.UserDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "BOARD_COMMENT")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_no")
    private Board board;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private String authorId;

    @Column(nullable = false)
    private String authorName; //익명 게시판의 경우 '익명' 표시

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    private boolean anonymous = false;

    public void markCreated(UserDTO user) {
        super.setCreatedBy(user.getUsername());
        super.setUpdatedBy(user.getUsername());
        super.setCreatedAt(LocalDateTime.now());
        super.setUpdatedAt(LocalDateTime.now());
    }

    public void markUpdated(UserDTO user) {
        super.setUpdatedBy(user.getUsername());
        super.setUpdatedAt(LocalDateTime.now());
    }
}
