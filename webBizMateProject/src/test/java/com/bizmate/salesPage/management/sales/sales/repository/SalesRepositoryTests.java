package com.bizmate.salesPage.management.sales.sales.repository;

import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.bizmate.salesPages.management.sales.sales.repository.SalesRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Slf4j
public class SalesRepositoryTests {
    @Autowired
    private SalesRepository salesRepository;

    @Test
    public void testSalesInsert() {
        for(int i = 1; i<= 10; i++) {
            Sales sales = Sales.builder()
                    .salesId("20251007-" + i)
                    .projectId("210415-" + i)
                    .projectName("테스트용" + i)
                    .deploymentDate(LocalDate.now())
                    .salesAmount(new BigDecimal(i + "2500000"))
                    .userId("user00" + i)
                    .salesNote("테스트!")
                    .writer("한유주")
                    .clientId("132-86-156477")
                    .clientCompany("테스트기업" + i)
                    .build();

            log.info("### sales 테이블에 첫번째 데이터 입력");
            salesRepository.save(sales);
        }
    }

    @Transactional
    @Test
    public void salesListTest() {
        List<Sales> salesList = salesRepository.findAll();
        salesList.forEach(sales -> log.info(sales.toString()));
    }
}
