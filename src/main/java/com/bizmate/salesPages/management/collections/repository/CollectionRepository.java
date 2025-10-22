package com.bizmate.salesPages.management.collections.repository;

import com.bizmate.salesPages.management.collections.domain.Collection;
import com.bizmate.salesPages.report.salesReport.dto.CollectionSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, String>, CollectionRepositoryCustom {

    @Query("SELECT MAX(c.collectionId) FROM Collection c WHERE c.collectionDate = :collectionDate")
    Optional<String> findMaxCollectionIdByCollectionDate(@Param("collectionDate")LocalDate today);

    @Query("SELECT c.collectionId FROM Collection c ORDER BY c.collectionId ASC LIMIT 1")
    List<String> findMinCollectionId();

    @Query("""
        SELECT new com.bizmate.salesPages.report.salesReport.dto.CollectionSummary(
             c.client.clientId, c.client.clientCompany, SUM(c.collectionMoney)
        ) 
        FROM Collection c 
        GROUP BY c.client.clientId, c.client.clientCompany 
        ORDER BY SUM(c.collectionMoney) DESC
        """
    )
    List<CollectionSummary> findTotalCollectionAmountGroupByClient();
}

