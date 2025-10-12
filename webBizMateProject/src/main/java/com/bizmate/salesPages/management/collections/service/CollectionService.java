package com.bizmate.salesPages.management.collections.service;

import com.bizmate.salesPages.common.dto.PageRequestDTO;
import com.bizmate.salesPages.common.dto.PageResponseDTO;
import com.bizmate.salesPages.management.collections.dto.CollectionDTO;

public interface CollectionService {
    public String register(CollectionDTO collectionDTO);
    public CollectionDTO get(String collectionId);
    public void modify(CollectionDTO collectionDTO);
    public void remove(String collectionId);
    public PageResponseDTO<CollectionDTO> list(PageRequestDTO pageRequestDTO);
}
