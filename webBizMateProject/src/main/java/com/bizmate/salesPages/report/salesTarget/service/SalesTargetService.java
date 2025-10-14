package com.bizmate.salesPages.report.salesTarget.service;

import com.bizmate.salesPages.common.dto.PageRequestDTO;
import com.bizmate.salesPages.common.dto.PageResponseDTO;
import com.bizmate.salesPages.report.salesTarget.dto.SalesTargetDTO;

public interface SalesTargetService {
    public Long register(SalesTargetDTO salesTargetDTO);
    public SalesTargetDTO get(Long targetId);
    public void modify(SalesTargetDTO salesTargetDTO);
    public void remove(Long targetId);
    public PageResponseDTO<SalesTargetDTO> list(PageRequestDTO pageRequestDTO);
}
