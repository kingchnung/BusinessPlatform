package com.bizmate.salesPages.client.controller;

import com.bizmate.common.dto.PageRequestDTO;
import com.bizmate.common.dto.PageResponseDTO;
import com.bizmate.salesPages.client.dto.ClientDTO;
import com.bizmate.salesPages.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @GetMapping("/{clientNo}")
    public ClientDTO get(@PathVariable(name = "clientNo") Long clientNo){
        return clientService.clientGet(clientNo);
    }

    @GetMapping("/clientList")
    public PageResponseDTO<ClientDTO> list(PageRequestDTO pageRequestDTO){
        return clientService.clientList(pageRequestDTO);
    }

    @PostMapping(value = "/")
    public Map<String,Long> register(@RequestBody ClientDTO clientDTO){
        Long clientNo = clientService.clientRegister(clientDTO);
        return Map.of("ClientNo",clientNo);
    }

    @PutMapping("/{clientNo}")
    public Map<String,String> modify(@PathVariable(name = "clientNo")Long clientNo, @RequestBody ClientDTO clientDTO){
        clientDTO.setClientNo(clientNo);
        clientService.clientModify(clientDTO);
        return Map.of("RESULT","SUCCESS");
    }

    @DeleteMapping("/{clientNo}")
    public Map<String,String> remove(@PathVariable(name = "clientNo") Long clientNo){
        clientService.clientRemove(clientNo);
        return Map.of("RESULT","SUCCESS");
    }
}