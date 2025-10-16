package com.bizmate.salesPages.client.service;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.client.dto.ClientDTO;
import org.springframework.stereotype.Service;

@Service
public interface ClientService {
    Long clientRegister(ClientDTO clientDTO);
    ClientDTO clientGet(Long clientNo);
    void clientModify(ClientDTO clientDTO);
    void clientRemove(Long clientNo);
    PageResponseDTO<ClientDTO> clientList(PageRequestDTO pageRequestDTO);
}
