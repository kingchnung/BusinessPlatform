package com.bizmate.salesPages.report.salesTarget.service;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.report.salesTarget.dto.SalesTargetDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalesTargetServiceImpl implements SalesTargetService{
    @Override
    public void remove(Long targetId) {

    }

    @Override
    public void modify(SalesTargetDTO salesTargetDTO) {

    }

    @Override
    public Long register(SalesTargetDTO salesTargetDTO) {
        return 0L;
    }

    @Override
    public PageResponseDTO<SalesTargetDTO> list(PageRequestDTO pageRequestDTO) {
        return null;
    }

    @Override
    public SalesTargetDTO get(Long targetId) {
        return null;
    }
}
