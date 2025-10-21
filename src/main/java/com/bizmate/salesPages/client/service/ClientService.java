package com.bizmate.salesPages.client.service;


import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.client.dto.ClientDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClientService {
    Long clientRegister(ClientDTO clientDTO);

//    List<String> register(List<ClientDTO> clientDTOList);

    ClientDTO clientGet(Long clientNo);
    void clientModify(ClientDTO clientDTO);
    void clientRemove(Long clientNo);
    PageResponseDTO<ClientDTO> clientList(PageRequestDTO pageRequestDTO);

}
