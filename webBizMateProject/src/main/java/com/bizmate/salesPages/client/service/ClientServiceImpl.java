package com.bizmate.salesPages.client.service;

import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.salesPages.common.dto.PageRequestDTO;
import com.bizmate.salesPages.common.dto.PageResponseDTO;
import com.bizmate.salesPages.client.domain.Client;
import com.bizmate.salesPages.client.dto.ClientDTO;
import com.bizmate.salesPages.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService{
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long clientRegister(ClientDTO clientDTO) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDTO userDTO){
            clientDTO.setUserId(userDTO.getUsername());
            clientDTO.setWriter(userDTO.getEmpName());
        } else {
            throw new IllegalStateException("주문 등록을 위한 사용자 인증 정보를 찾을 수 없습니다. (비정상 접근)");
        }

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
        Optional<Client> result = clientRepository.findById(clientDTO.getClientNo());
        Client client = result.orElseThrow();

        client.changeClientId(clientDTO.getClientId());
        client.changeClientCompany(clientDTO.getClientCompany());
        client.changeClientCeo(clientDTO.getClientCeo());
        client.changeClientBusinessType(clientDTO.getClientBusinessType());
        client.changeClientAddress(clientDTO.getClientAddress());
        client.changeClientContact(clientDTO.getClientContact());
        client.changeClientNote(clientDTO.getClientNote());
        client.changeBusinessLicenseFile(clientDTO.getBusinessLicenseFile());
        client.changeClientEmail(clientDTO.getClientEmail());

        clientRepository.save(client);
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
            case  "userId" :
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
