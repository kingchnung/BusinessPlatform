package com.bizmate.salesPages.client.service;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.client.dto.ClientDTO;
import org.springframework.stereotype.Service;

@Service
public interface ClientService {
    public Long clientRegister(ClientDTO clientDTO);
    public ClientDTO clientGet(Long clientNo);
    public void clientModify(ClientDTO clientDTO);
    public void clientRemove(Long clientNo);
    public PageResponseDTO<ClientDTO> clientList(PageRequestDTO pageRequestDTO);

}
