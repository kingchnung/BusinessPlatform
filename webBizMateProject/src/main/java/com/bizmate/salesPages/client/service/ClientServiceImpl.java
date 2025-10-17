package com.bizmate.salesPages.client.service;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.util.FileUtil;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.hr.security.UserPrincipal;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService{
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;
    private final FileUtil fileUtil;

//    @Override
//    public Long clientRegister(ClientDTO clientDTO) {
//        Optional<Client> existingClient = clientRepository.findByClientId(clientDTO.getClientId());
//
//        if (existingClient.isPresent()) {
//            throw new IllegalStateException("이미 등록된 사업자번호입니다.");
//        }
//
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if(principal instanceof UserDTO userDTO){
//            clientDTO.setUserId(userDTO.getUsername());
//            clientDTO.setWriter(userDTO.getEmpName());
//        } else {
//            throw new IllegalStateException("주문 등록을 위한 사용자 인증 정보를 찾을 수 없습니다. (비정상 접근)");
//        }
//
//        Client client = modelMapper.map(clientDTO, Client.class);
//
//        Client savedClient = clientRepository.save(client);
//        return savedClient.getClientNo();
//    }
    //    @Override
//    public void clientModify(ClientDTO clientDTO) {
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
//        client.changeClientEmail(clientDTO.getClientEmail());
//
//        clientRepository.save(client);
//    }
    private String formatClientId(String clientId) {
        if (clientId == null) {
            return null;
        }
        String rawId = clientId.replaceAll("-", "");

        if (rawId.length() == 10) {
            return rawId.substring(0, 3) + "-" + rawId.substring(3, 5) + "-" + rawId.substring(5, 10);
        }

        // 10자리가 아니면 (잘못된 데이터면) 원본을 그대로 반환
        return clientId;
    }

    @Override
public Long clientRegister(ClientDTO clientDTO, MultipartFile file) { // 👇 [수정] MultipartFile 파라미터 받기
    Optional<Client> existingClient = clientRepository.findByClientId(clientDTO.getClientId());

    if (existingClient.isPresent()) {
        throw new IllegalStateException("이미 등록된 사업자번호입니다.");
    }

    // --- 파일 저장 로직 시작 ---
    try {
        String savedFilename = fileUtil.saveFile(file);
        if (savedFilename != null) {
            clientDTO.setBusinessLicenseFile(savedFilename);
        }
    } catch (IOException e) {
        // 로그를 남기거나 사용자 정의 예외를 던질 수 있습니다.
        throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
    }
    // --- 파일 저장 로직 끝 ---

    String formattedId = formatClientId(clientDTO.getClientId());
    clientDTO.setClientId(formattedId);

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserPrincipal userPrincipal) {
        clientDTO.setUserId(userPrincipal.getUsername());
//        clientDTO.setWriter(userPrincipal.getEmpName());
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
    public void clientModify(ClientDTO clientDTO, MultipartFile file) {
        Optional<Client> result = clientRepository.findById(clientDTO.getClientNo());
        Client client = result.orElseThrow();

        try {
            String savedFilename = fileUtil.saveFile(file);
            if (savedFilename != null) {
                client.changeBusinessLicenseFile(savedFilename);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }

        String formattedId = formatClientId(clientDTO.getClientId());
        client.changeClientId(formattedId); // DTO가 아닌 Entity에 바로 적용

        client.changeClientCompany(clientDTO.getClientCompany());
        client.changeClientEmail(clientDTO.getClientEmail());
        client.changeClientAddress(clientDTO.getClientAddress());
        client.changeClientContact(clientDTO.getClientContact());
        client.changeClientNote(clientDTO.getClientNote());
        client.changeClientBusinessType(clientDTO.getClientBusinessType());
        client.changeClientCeo(clientDTO.getClientCeo());


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

    @Override
    public void clientRemoveList(List<Long> clientNos) {
        clientRepository.deleteAllByIdInBatch(clientNos);
    }
}
