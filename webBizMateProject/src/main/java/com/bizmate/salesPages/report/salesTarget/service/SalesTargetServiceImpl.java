package com.bizmate.salesPages.report.salesTarget.service;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.hr.security.UserPrincipal;
import com.bizmate.salesPages.report.salesTarget.domain.SalesTarget;
import com.bizmate.salesPages.report.salesTarget.dto.SalesTargetDTO;
import com.bizmate.salesPages.report.salesTarget.repository.SalesTargetRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesTargetServiceImpl implements SalesTargetService{
    private final SalesTargetRepository salesTargetRepository;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final ModelMapper modelMapper;

    @Override
    public Long register(SalesTargetDTO salesTargetDTO) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String writer = userPrincipal.getUsername();
        String userId = userPrincipal.getUserId().toString();

        SalesTarget salesTarget = modelMapper.map(salesTargetDTO, SalesTarget.class);
        salesTarget.setWriter(writer);
        salesTarget.setUserId(userId);

        SalesTarget savedSalesTarget = salesTargetRepository.save(salesTarget);
        return savedSalesTarget.getTargetId();
    }

    @Override
    public SalesTargetDTO get(Long targetId) {
        Optional<SalesTarget> result = salesTargetRepository.findById(targetId);
        SalesTarget salesTarget = result.orElseThrow();
        SalesTargetDTO dto = modelMapper.map(salesTarget, SalesTargetDTO.class);
        return dto;
    }

    @Override
    public void modify(SalesTargetDTO salesTargetDTO) {
        Optional<SalesTarget> result = salesTargetRepository.findById(salesTargetDTO.getTargetId());
        SalesTarget salesTarget = result.orElseThrow();

        salesTarget.changTargetYear(salesTargetDTO.getTargetYear());
        salesTarget.changeTargetMonth(salesTargetDTO.getTargetMonth());
        salesTarget.changeTargetAmount(salesTargetDTO.getTargetAmount());

        salesTargetRepository.save(salesTarget);
    }

    @Override
    public void remove(Long targetId) {
        salesTargetRepository.deleteById(targetId);
    }

    @Override
    public PageResponseDTO<SalesTargetDTO> list(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage()-1,
                pageRequestDTO.getSize(),
                Sort.by("targetId").descending());

        Page<SalesTarget> result = salesTargetRepository.findAll(pageable);
        List<SalesTargetDTO> dtoList = result.getContent().stream().map(
                salesTarget -> modelMapper.map(salesTarget, SalesTargetDTO.class)).collect(Collectors.toList());
        long totalCount = result.getTotalElements();

        PageResponseDTO<SalesTargetDTO> responseDTO = PageResponseDTO.<SalesTargetDTO>withAll().dtoList(dtoList).pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();

        return responseDTO;
    }
}
