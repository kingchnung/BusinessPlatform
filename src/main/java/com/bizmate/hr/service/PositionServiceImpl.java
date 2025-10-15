package com.bizmate.hr.service;

import com.bizmate.hr.domain.code.Position;
import com.bizmate.hr.dto.code.PositionDTO;
import com.bizmate.hr.dto.code.PositionRequestDTO;
import com.bizmate.hr.repository.PositionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PositionServiceImpl implements PositionService {

    // 필요한 Repository만 주입
    private final PositionRepository positionRepository;

    @Override
    public PositionDTO savePosition(Long positionCode, PositionRequestDTO requestDTO) {
        Position position;

        if (positionCode != null) {
            // 수정 로직: PK로 기존 엔티티 조회
            position = positionRepository.findById(positionCode)
                    .orElseThrow(() -> new EntityNotFoundException("직책 코드 " + positionCode + "를 찾을 수 없습니다."));
        } else {
            // 등록 로직: 새 엔티티 생성
            position = new Position();
        }

        // DTO 필드 반영
        position.setPositionName(requestDTO.getPositionName());
        position.setDescription(requestDTO.getDescription());
        // isUsed 필드는 Entity에서 기본값 "Y"가 설정되어 있음

        Position savedPosition = positionRepository.save(position);
        return PositionDTO.fromEntity(savedPosition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PositionDTO> getAllPositions() {
        return positionRepository.findAll().stream()
                .map(PositionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PositionDTO getPosition(Long positionCode) {
        Position position = positionRepository.findById(positionCode)
                .orElseThrow(() -> new EntityNotFoundException("직책 코드 " + positionCode + "를 찾을 수 없습니다."));
        return PositionDTO.fromEntity(position);
    }

    @Override
    public void deletePosition(Long positionCode) {
        positionRepository.deleteById(positionCode);
    }
}