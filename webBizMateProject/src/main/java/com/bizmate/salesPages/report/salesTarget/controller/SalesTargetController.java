package com.bizmate.salesPages.report.salesTarget.controller;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.report.salesTarget.dto.SalesTargetDTO;
import com.bizmate.salesPages.report.salesTarget.service.SalesTargetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/salesTarget")
@RequiredArgsConstructor
public class SalesTargetController {
    private final SalesTargetService salesTargetService;

    @GetMapping("/{targetId}")
    public SalesTargetDTO get(@PathVariable(name = "targetId") Long targetId){
        return salesTargetService.get(targetId);
    }

    @GetMapping("/salesTargetList")
    public PageResponseDTO<SalesTargetDTO> list(PageRequestDTO pageRequestDTO){
        return salesTargetService.list(pageRequestDTO);
    }

    @PostMapping("/")
    public Map<String, Long> register(@RequestBody SalesTargetDTO salesTargetDTO){
        Long targetId = salesTargetService.register(salesTargetDTO);
        return Map.of("TargetId", targetId);
    }

    @PutMapping("/{targetId}")
    public Map<String, String> modify(@PathVariable(name = "targetId")Long targetId, @RequestBody SalesTargetDTO salesTargetDTO){
        salesTargetDTO.setTargetId(targetId);
        salesTargetService.modify(salesTargetDTO);
        return Map.of("RRESULT","SUCCESS");
    }

    @DeleteMapping("/{targetId}")
    public Map<String, String> remove(@PathVariable(name = "targetId")Long targetId){
        salesTargetService.remove(targetId);
        return Map.of("RRESULT","SUCCESS");
    }
}
