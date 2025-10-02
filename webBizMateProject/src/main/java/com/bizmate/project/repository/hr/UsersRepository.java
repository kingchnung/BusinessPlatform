package com.bizmate.project.repository.hr;

import com.bizmate.project.domain.hr.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users,Integer> {
}
