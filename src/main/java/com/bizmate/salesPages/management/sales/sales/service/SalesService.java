package com.bizmate.salesPages.management.sales.sales.service;

import com.bizmate.salesPages.common.dto.PageRequestDTO;
import com.bizmate.salesPages.common.dto.PageResponseDTO;
import com.bizmate.salesPages.management.sales.sales.dto.SalesDTO;
import org.springframework.stereotype.Service;

@Service
public interface SalesService {
    String register(SalesDTO salesDTO);
    SalesDTO get(String salesId);
    void modify(SalesDTO salesDTO);
    void remove(String salesId);
    PageResponseDTO<SalesDTO> list(PageRequestDTO pageRequestDTO);
}

