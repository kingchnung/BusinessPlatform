package com.bizmate.hr.controller;



import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {

        UserDTO userDTO = userService.getUser(userId);

        return ResponseEntity.ok(userDTO);
    }
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getMyInfo() {
        UserDTO currentUser = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(currentUser);
    }
}
