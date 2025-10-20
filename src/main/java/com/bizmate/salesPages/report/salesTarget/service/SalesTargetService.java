package com.bizmate.salesPages.report.salesTarget.service;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.report.salesTarget.dto.SalesTargetDTO;

public interface SalesTargetService {
    void remove(Long targetId);

    void modify(SalesTargetDTO salesTargetDTO);

    Long register(SalesTargetDTO salesTargetDTO);

    PageResponseDTO<SalesTargetDTO> list(PageRequestDTO pageRequestDTO);

    SalesTargetDTO get(Long targetId);
}
