package com.bizmate.salesPages.client.repository;

import com.bizmate.salesPages.client.domain.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByClientNo(Long clientNo);
    Optional<Client> findByClientId(String clientId);

//    List<Client> findByClientIdContaining(String clientId);
//    List<Client> findByClientCompanyContaining(String clientCompany);
//    List<Client> findByClientCeoContaining(String clientCeo);
//    List<Client> findByClientContactContaining(String clientContact);
//    List<Client> findByEmpNameContaining(String empName);

    @Query(value = "SELECT c FROM Client c ORDER BY NLSSORT(c.clientCompany, 'NLS_SORT=KOREAN') DESC")
    List<Client> findByOrderByClientCompanyDesc();

    Page<Client> findAll(Pageable pageable);

    Page<Client> findByClientIdContaining(String clientId, Pageable pageable);
    Page<Client> findByClientCompanyContaining(String clientCompany, Pageable pageable);
    Page<Client> findByClientCeoContaining(String clientCeo, Pageable pageable);
    Page<Client> findByClientContactContaining(String clientContact, Pageable pageable);
    Page<Client> findByEmpNameContaining(String empName, Pageable pageable);

}
