package com.bizmate.hr.repository;

import com.bizmate.hr.domain.Employee;
import com.bizmate.hr.domain.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    /**
     * username(로그인 ID)을 통해 User 정보와 연관된 Role, Permission 정보를 한 번에 로드합니다.
     * User 엔티티의 username 필드가 로그인 ID로 사용됩니다.
     */
    @EntityGraph(attributePaths = {"roles", "roles.permissions", "employee"})
    @Query("select u from UserEntity u where u.username = :username")
    UserEntity getWithRolesAndPermissions(@Param("username") String username);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<UserEntity> findByEmployee(Employee employee);
}
