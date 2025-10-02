package com.bizmate.hr.service;

import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.dto.user.UserDTO;

public interface UserService {

    UserDTO createUserAccount(Employee employee, String initialPassword);
    UserDTO getUser(Long userId);
}
