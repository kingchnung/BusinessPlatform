package com.bizmate.salesPages.client.service;





import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.client.domain.Client;
import com.bizmate.salesPages.client.dto.ClientDTO;
import com.bizmate.salesPages.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService{
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

//    @Override
//    public List<String> register(List<ClientDTO> clientDTOList) {
//        List<String> registeredClientIds = new ArrayList<>();
//
//        for (ClientDTO clientDTO : clientDTOList) {
//            Client client = modelMapper.map(clientDTO, Client.class);
//
//            Client savedClient = clientRepository.save(client);
//            registeredClientIds.add(savedClient.getClientId());
//        }
//
//        return registeredClientIds;
//    }

    @Override
    public Long clientRegister(ClientDTO clientDTO) {
        Client client = modelMapper.map(clientDTO, Client.class);
        Client savedClient = clientRepository.save(client);
        return savedClient.getClientNo();
    }

    @Override
    public ClientDTO clientGet(Long clientNo){
        Optional<Client> result = clientRepository.findById(clientNo);
        Client client = result.orElseThrow();
        ClientDTO dto = modelMapper.map(client, ClientDTO.class);
        return dto;
    }

    @Override
    public void clientModify(ClientDTO clientDTO) {
//        Optional<Client> result = clientRepository.findById(clientDTO.getClientNo());
//        Client client = result.orElseThrow();
//
//        client.changeClientId(clientDTO.getClientId());
//        client.changeClientCompany(clientDTO.getClientCompany());
//        client.changeClientCeo(clientDTO.getClientCeo());
//        client.changeClientBusinessType(clientDTO.getClientBusinessType());
//        client.changeClientAddress(clientDTO.getClientAddress());
//        client.changeClientContact(clientDTO.getClientContact());
//        client.changeClientNote(clientDTO.getClientNote());
//        client.changeBusinessLicenseFile(clientDTO.getBusinessLicenseFile());
//        client.changeEmpName(clientDTO.getEmpName());
//        client.changeClientEmail(clientDTO.getClientEmail());
//        client.changeUserId(clientDTO.getUserId());
//
//        clientRepository.save(client);
    }

    @Override
    public void clientRemove(Long clientNo) {
        clientRepository.deleteById(clientNo);
    }

    @Override
    public PageResponseDTO<ClientDTO> clientList(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() -1,
                pageRequestDTO.getSize(),
                Sort.by("clientNo").descending());

        Page<Client> result;

        switch (pageRequestDTO.getSearch()){
            case "clientId" :
                result = clientRepository.findByClientIdContaining(pageRequestDTO.getKeyword(),pageable);
                break;
            case "clientCompany" :
                result = clientRepository.findByClientCompanyContaining(pageRequestDTO.getKeyword(), pageable);
                break;
            case "clientCeo" :
                result = clientRepository.findByClientCeoContaining(pageRequestDTO.getKeyword(), pageable);
                break;
            case "clientContact" :
                result = clientRepository.findByClientContactContaining(pageRequestDTO.getKeyword(),pageable);
                break;
            case  "empName" :
                result = clientRepository.findByUserIdContaining(pageRequestDTO.getKeyword(),pageable);
                break;
            default:
                result = clientRepository.findAll(pageable);
                break;
        }

        List<ClientDTO> dtoList = result.getContent().stream().map(
                client -> modelMapper.map(client, ClientDTO.class)).collect(Collectors.toList());
        long totalCount = result.getTotalElements();

        PageResponseDTO<ClientDTO> responseDTO = PageResponseDTO.<ClientDTO>withAll().dtoList(dtoList).pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();

        return responseDTO;
    }
}
