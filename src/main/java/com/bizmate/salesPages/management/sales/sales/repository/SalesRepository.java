package com.bizmate.salesPages.management.sales.sales.repository;

import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.bizmate.salesPages.report.salesReport.dto.*;
import org.springframework.data.envers.repository.support.EnversRevisionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalesRepository extends JpaRepository<Sales, String>, SalesRepositoryCustom , EnversRevisionRepository<Sales, String, Integer> {

    @Query("SELECT MAX(s.salesId) FROM Sales s WHERE s.salesIdDate = :salesIdDate")
    Optional<String> findMaxSalesIdBySalesIdDate(@Param("salesIdDate") LocalDate today);

    @Query("SELECT s.salesId FROM Sales s ORDER BY s.salesId ASC LIMIT 1")
    Optional<String> findMinSalesId();

    @Query("""
        SELECT new com.bizmate.salesPages.report.salesReport.dto.ClientSalesSummary(
            s.clientId, s.clientCompany, SUM(s.salesAmount)
        ) 
        FROM Sales s 
        WHERE s.invoiceIssued = true 
        GROUP BY s.clientId, s.clientCompany 
        ORDER BY SUM(s.salesAmount) DESC
        """
    )
    List<ClientSalesSummary> findTotalSalesAmountGroupByClient();

    @Query("""
            SELECT new com.bizmate.salesPages.report.salesReport.dto.ProjectSalesSummary(
                s.projectId, s.projectName, SUM(s.salesAmount)
            ) 
            FROM Sales s 
            WHERE s.invoiceIssued = true 
            GROUP BY s.projectId, s.projectName 
            ORDER BY SUM(s.salesAmount) DESC
            """)
    List<ProjectSalesSummary> findTotalSalesAmountGroupByProject();

    @Query(""" 
        SELECT new com.bizmate.salesPages.report.salesReport.dto.QuarterlySalesSummary(
            CAST(FUNCTION('TO_CHAR', s.salesDate, 'YYYY') AS integer),
            CAST(FUNCTION('TO_CHAR', s.salesDate, 'Q') AS integer),
            SUM(s.salesAmount)
        ) 
        FROM Sales s 
        WHERE s.invoiceIssued = true 
        GROUP BY FUNCTION('TO_CHAR', s.salesDate, 'YYYY'), FUNCTION('TO_CHAR', s.salesDate, 'Q') 
        ORDER BY FUNCTION('TO_CHAR', s.salesDate, 'YYYY') DESC, FUNCTION('TO_CHAR', s.salesDate, 'Q') DESC
        """)
    List<QuarterlySalesSummary> findTotalSalesAmountGroupByQuarter();

    @Query("SELECT SUM(s.salesAmount) FROM Sales s WHERE s.order.orderId = :orderId AND s.invoiceIssued = true")
    BigDecimal findSumOfIssuedSalesByOrderId(@Param("orderId") String orderId);

    boolean existsByOrderOrderId(String orderId);

    List<Sales> findByClientIdOrderBySalesDateDesc(String clientId);

    // 특정 연도의 월별 매출 합계 (기간별 현황 탭)
    @Query(""" 
        SELECT new com.bizmate.salesPages.report.salesReport.dto.MonthlySalesSummary(
            CAST(FUNCTION('TO_CHAR', s.salesDate, 'YYYY') AS integer),
            CAST(FUNCTION('TO_CHAR', s.salesDate, 'MM') AS integer),
            SUM(s.salesAmount)
        ) 
        FROM Sales s 
        WHERE s.invoiceIssued = true 
        AND CAST(FUNCTION('TO_CHAR', s.salesDate, 'YYYY') AS integer) = :year 
        GROUP BY FUNCTION('TO_CHAR', s.salesDate, 'YYYY'), FUNCTION('TO_CHAR', s.salesDate, 'MM') 
        ORDER BY FUNCTION('TO_CHAR', s.salesDate, 'MM') ASC
        """)
    List<MonthlySalesSummary> findMonthlySalesSummaryByYear(@Param("year") Integer year);


    // 특정 연월의 거래처별 매출 합계 (거래처별 현황 탭)
    @Query("""
        SELECT new com.bizmate.salesPages.report.salesReport.dto.ClientSalesSummary(
            s.clientId, s.clientCompany, SUM(s.salesAmount)
        ) 
        FROM Sales s 
        WHERE s.invoiceIssued = true 
        AND FUNCTION('TO_CHAR', s.salesDate, 'YYYY-MM') = :yearMonth 
        GROUP BY s.clientId, s.clientCompany 
        """)
    List<ClientSalesSummary> findClientSalesSummaryByYearMonth(@Param("yearMonth") String yearMonth);

    // 특정 연도의 거래처별 매출 합계 (거래처별 현황 - '월' 전체)
    @Query("""
        SELECT new com.bizmate.salesPages.report.salesReport.dto.ClientSalesSummary(
            s.clientId, s.clientCompany, SUM(s.salesAmount)
        ) 
        FROM Sales s 
        WHERE s.invoiceIssued = true 
        AND FUNCTION('TO_CHAR', s.salesDate, 'YYYY') = :year 
        GROUP BY s.clientId, s.clientCompany 
        """)
    List<ClientSalesSummary> findClientSalesSummaryByYear(@Param("year") String year);

    // 연도별 총 매출 (연도별 요약)
    @Query("""
        SELECT new com.bizmate.salesPages.report.salesReport.dto.YearlySalesSummary(
            CAST(FUNCTION('TO_CHAR', s.salesDate, 'YYYY') AS integer),
            SUM(s.salesAmount)
        ) 
        FROM Sales s 
        WHERE s.invoiceIssued = true 
        GROUP BY FUNCTION('TO_CHAR', s.salesDate, 'YYYY')
        ORDER BY FUNCTION('TO_CHAR', s.salesDate, 'YYYY') DESC
        """)
    List<YearlySalesSummary> findYearlySalesSummary();
}
