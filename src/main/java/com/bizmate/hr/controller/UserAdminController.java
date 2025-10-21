package com.bizmate.hr.controller;


import com.bizmate.hr.dto.member.ResetPasswordRequest;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.dto.user.UserUpdateRequestDTO;
import com.bizmate.hr.service.AuthService;
import com.bizmate.hr.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAnyRole('CEO', 'ADMIN', 'MANAGER')")
public class UserAdminController {
    private final UserService userService;


    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateRequestDTO updateDTO
    ) {
        log.info("🛠️ [ADMIN] 사용자 정보 수정 요청: userId={}", userId);
        UserDTO updated = userService.updateUser(userId, updateDTO);
        return ResponseEntity.ok(updated);
    }



    @PostMapping("/{userId}/unlock")
    public ResponseEntity<String> resetUserLock(@PathVariable Long userId) {
        log.info("🔓 [ADMIN] 사용자 계정 잠금 해제 요청: userId={}", userId);
        String tempPw = userService.resetUserLock(userId);
        return ResponseEntity.ok("계정이 잠금 해제되었으며 임시 비밀번호(" + tempPw + ")가 이메일로 발송되었습니다.");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("🗑️ [ADMIN] 사용자 계정 삭제 요청: userId={}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
