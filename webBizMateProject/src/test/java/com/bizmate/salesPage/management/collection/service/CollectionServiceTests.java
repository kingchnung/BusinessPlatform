package com.bizmate.salesPage.management.collection.service;

import com.bizmate.salesPages.management.collections.dto.CollectionDTO;
import com.bizmate.salesPages.management.collections.service.CollectionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
@Slf4j
public class CollectionServiceTests {
    @Autowired
    private CollectionService collectionService;

    @Test
    public void testRegister() {
        for (int i = 1; i < 10; i++) {
            CollectionDTO collectionDTO = CollectionDTO.builder()
                    .collectionId("20250505-" + i)
                    .collectionNote("테스트용" + i)
                    .collectionMoney(new BigDecimal(i + "500000"))
                    .clientId("3035678901")
                    .clientCompany("테스트용" + i)
                    .build();

            String collectionId = collectionService.register(collectionDTO);
            log.info("생성된 주문 번호: {}", collectionId);
        }
    }

}
