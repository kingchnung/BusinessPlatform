package com.bizmate.hr.repository;

import com.bizmate.hr.domain.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository <Roles, Integer> {
}
