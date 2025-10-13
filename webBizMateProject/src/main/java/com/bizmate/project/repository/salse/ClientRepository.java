package com.bizmate.project.repository.salse;

import com.bizmate.project.domain.sails.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client,String> {
}
