package com.bizmate.salesPages.report.salesReport.service;


import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.salesPages.report.salesReport.dto.*;

import java.util.List;

public interface SalesReportService {
    List<CollectionSummary> getClientTotalCollectionSummary();

    List<ClientSalesSummary> getClientTotalSalesSummary();
    List<ProjectSalesSummary> getProjectTotalSalesSummary();
    List<QuarterlySalesSummary> getQuarterlyTotalSalesSummary();
    List<ClientReceivablesDTO> getClientReceivablesSummary();

    com.bizmate.common.page.PageResponseDTO<ClientSalesStatusDTO> getClientSalesStatus(
            com.bizmate.common.page.PageRequestDTO pageRequestDTO, Integer year, Integer month
    );

    List<PeriodSalesStatusDTO> getPeriodSalesStatus(Integer year);

    List<YearlySalesStatusDTO> getYearlySalesStatus();
}
