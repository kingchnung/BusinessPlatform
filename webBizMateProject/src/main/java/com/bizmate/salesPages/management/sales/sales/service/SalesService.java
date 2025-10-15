package com.bizmate.salesPages.management.sales.sales.service;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.management.sales.sales.dto.SalesDTO;
<<<<<<< HEAD

public interface SalesService {
    public String register(SalesDTO salesDTO);
    public SalesDTO get(String salesId);
    public void modify(SalesDTO salesDTO);
    public void remove(String salesId);
    public PageResponseDTO<SalesDTO> list(PageRequestDTO pageRequestDTO);
=======
import org.springframework.stereotype.Service;

@Service
public interface SalesService {
    String register(SalesDTO salesDTO);
    SalesDTO get(String salesId);
    void modify(SalesDTO salesDTO);
    void remove(String salesId);
    PageResponseDTO<SalesDTO> list(PageRequestDTO pageRequestDTO);
>>>>>>> 7e631613e802f528445a8f222c1ec078e9c8bda3
}

