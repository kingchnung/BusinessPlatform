package com.bizmate.hr.repository;

import com.bizmate.hr.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository <Users, Long> {
    Users findByUserId(Long userName);
}
