package com.bizmate.salesPages.management.collections.controller;

import com.bizmate.salesPages.common.dto.PageRequestDTO;
import com.bizmate.salesPages.common.dto.PageResponseDTO;
import com.bizmate.salesPages.management.collections.dto.CollectionDTO;
import com.bizmate.salesPages.management.collections.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/collection")
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionService collectionService;

    @GetMapping("/{collectionId}")
    public CollectionDTO get(@PathVariable(name = "collectionId") String collectionId){
        return collectionService.get(collectionId);
    }

    @GetMapping("/collectionList")
    public PageResponseDTO<CollectionDTO> List(PageRequestDTO pageRequestDTO){
        return collectionService.list(pageRequestDTO);
    }

    @PostMapping(value = "/")
    public Map<String, String> register(@RequestBody CollectionDTO collectionDTO){
        String collectionId = collectionService.register(collectionDTO);
        return Map.of("CollectionId", collectionId);
    }

    @PutMapping("/{collectionId}")
    public Map<String,String> modify(@PathVariable(name = "collectionId")String collectionId, @RequestBody CollectionDTO collectionDTO){
        collectionDTO.setCollectionId(collectionId);
        collectionService.modify(collectionDTO);
        return Map.of("RESULT", "SUCCESS");
    }

    @DeleteMapping("/{collectionId}")
    public Map<String, String> remove(@PathVariable(name = "collectionId")String collectionId){
        collectionService.remove(collectionId);
        return Map.of("RESULT", "SUCCESS");
    }
}
