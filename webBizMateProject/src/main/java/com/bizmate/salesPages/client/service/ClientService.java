package com.bizmate.salesPages.client.service;

import com.bizmate.salesPages.common.dto.PageRequestDTO;
import com.bizmate.salesPages.common.dto.PageResponseDTO;
import com.bizmate.salesPages.client.dto.ClientDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClientService {
    public Long clientRegister(ClientDTO clientDTO);

//    List<String> register(List<ClientDTO> clientDTOList);

    public ClientDTO clientGet(Long clientNo);
    public void clientModify(ClientDTO clientDTO);
    public void clientRemove(Long clientNo);
    public PageResponseDTO<ClientDTO> clientList(PageRequestDTO pageRequestDTO);

}
