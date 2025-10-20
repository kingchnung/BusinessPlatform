package com.bizmate.salesPages.management.collections.service;



import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.management.collections.dto.CollectionDTO;
import com.bizmate.salesPages.report.salesReport.dto.*;

import java.util.List;

public interface CollectionService {
    public String register(CollectionDTO collectionDTO);
    public CollectionDTO get(String collectionId);
    public void modify(CollectionDTO collectionDTO);
    public void remove(String collectionId);
    public PageResponseDTO<CollectionDTO> list(PageRequestDTO pageRequestDTO);

    List<CollectionSummary> getClientTotalCollectionSummary();

    List<ClientSalesSummary> getClientTotalSalesSummary();
    List<ProjectSalesSummary> getProjectTotalSalesSummary();
    List<QuarterlySalesSummary> getQuarterlyTotalSalesSummary();
    List<ClientReceivablesDTO> getClientReceivablesSummary();
}
