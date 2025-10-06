package com.bizmate.project.repository.salse;

import com.bizmate.project.domain.sails.Client;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class ClinetTest {

    @Autowired
    private ClientRepository clientRepository;



    @Test
    public void selectCLient (){

        List<Client> clientList =  clientRepository.findAll();
        System.out.println("clientList = " + clientList);
        clientList.stream().forEach(client -> System.out.println("client.getClientCompany() = " + client.getClientCompany()) );



    }

}
