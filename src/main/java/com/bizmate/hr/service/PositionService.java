package com.bizmate.hr.service;

import com.bizmate.hr.dto.code.PositionDTO;
import com.bizmate.hr.dto.code.PositionRequestDTO;

import java.util.List;

public interface PositionService {
    // 직책코드를 Long 타입으로 받습니다. 신규 등록 시에는 null이 전달됩니다.
    PositionDTO savePosition(Long positionCode, PositionRequestDTO requestDTO);

    List<PositionDTO> getAllPositions();

    PositionDTO getPosition(Long positionCode);

    void deletePosition(Long positionCode);
}