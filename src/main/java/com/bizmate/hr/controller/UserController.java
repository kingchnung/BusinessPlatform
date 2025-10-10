package com.bizmate.hr.controller;



import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.dto.user.UserUpdateRequestDTO;
import com.bizmate.hr.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //  0. 전자결재용 - 모든 직원 목록 조회 (권한 제한 없음)
    @GetMapping("/list")
    public ResponseEntity<List<UserDTO>> getAllForApproval() {
        List<UserDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    // 1. 전체 사용자 목록 조회 (관리자용)
    @GetMapping
    @PreAuthorize("hasAuthority('sys:admin')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    // 2. 특정 사용자 정보 조회 (관리자용)
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('sys:admin')")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {

        UserDTO userDTO = userService.getUser(userId);

        return ResponseEntity.ok(userDTO);
    }

    // 3. 내 정보 조회 (인증된 모든 사용자용)
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getMyInfo() {
        UserDTO currentUser = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    // 4. 특정 사용자 정보 수정 (Update)
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('sys:admin')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequestDTO updateDTO) {
        UserDTO updatedUser = userService.updateUser(userId, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // 5. 특정 사용자 계정 삭제 (Delete)
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('sys:admin')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
