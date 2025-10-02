package com.bizmate.project.repository.hr;

import com.bizmate.project.domain.hr.Employees;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeesRepository extends JpaRepository<Employees, Integer> {
}
