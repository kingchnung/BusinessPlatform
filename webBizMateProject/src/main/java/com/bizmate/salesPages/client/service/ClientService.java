package com.bizmate.salesPages.client.service;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.client.dto.ClientDTO;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
@Service
public interface ClientService {
    public Long clientRegister(ClientDTO clientDTO);
    public ClientDTO clientGet(Long clientNo);
    public void clientModify(ClientDTO clientDTO);
    public void clientRemove(Long clientNo);
    public PageResponseDTO<ClientDTO> clientList(PageRequestDTO pageRequestDTO);
=======
import java.util.List;

@Service
public interface ClientService {
    Long clientRegister(ClientDTO clientDTO);

//    List<String> register(List<ClientDTO> clientDTOList);

    ClientDTO clientGet(Long clientNo);
    void clientModify(ClientDTO clientDTO);
    void clientRemove(Long clientNo);
    PageResponseDTO<ClientDTO> clientList(PageRequestDTO pageRequestDTO);
>>>>>>> 7e631613e802f528445a8f222c1ec078e9c8bda3

}
