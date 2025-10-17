package com.bizmate.salesPages.client.service;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.client.dto.ClientDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ClientService {
//    Long clientRegister(ClientDTO clientDTO);
    Long clientRegister(ClientDTO clientDTO, MultipartFile file);
    ClientDTO clientGet(Long clientNo);
//    void clientModify(ClientDTO clientDTO);
void clientModify(ClientDTO clientDTO, MultipartFile file);
    void clientRemove(Long clientNo);
    PageResponseDTO<ClientDTO> clientList(PageRequestDTO pageRequestDTO);

    void clientRemoveList(List<Long> clientNos);
}
