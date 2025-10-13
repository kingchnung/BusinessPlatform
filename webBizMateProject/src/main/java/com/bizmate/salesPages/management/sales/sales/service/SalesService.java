package com.bizmate.salesPages.management.sales.sales.service;

import com.bizmate.salesPages.common.dto.PageRequestDTO;
import com.bizmate.salesPages.common.dto.PageResponseDTO;
import com.bizmate.salesPages.management.sales.sales.dto.SalesDTO;
import org.springframework.stereotype.Service;

public interface SalesService {
    public String register(SalesDTO salesDTO);
    public SalesDTO get(String salesId);
    public void modify(SalesDTO salesDTO);
    public void remove(String salesId);
    public PageResponseDTO<SalesDTO> list(PageRequestDTO pageRequestDTO);
}

