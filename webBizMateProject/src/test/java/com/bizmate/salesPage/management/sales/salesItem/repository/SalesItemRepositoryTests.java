package com.bizmate.salesPage.management.sales.salesItem.repository;

import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.bizmate.salesPages.management.sales.sales.repository.SalesRepository;
import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
@Slf4j
public class SalesItemRepositoryTests {
    @Autowired
    private SalesRepository salesRepository;

    @Test
    @Transactional
    @Rollback(false)
    public  void salesItemInsertTest() {
        Sales sales = Sales.builder()
                .salesId("20251007-93")
                .projectId("210415")
                .projectName("saleItem테스트용 부들부들")
                .deploymentDate(LocalDate.now())
                .salesAmount(new BigDecimal( "2500000"))
                .userId("user00")
                .salesNote("테스트")
                .writer("한유주")
                .clientId("132-86-156477")
                .clientCompany("테스트기업")
                .build();

        SalesItem salesItem = SalesItem.builder()
                .itemName("테스트 품목1")
                .quantity(1L)
                .unitPrice(new BigDecimal("1504000"))
                .vat(new BigDecimal("15040"))
                .totalAmount(new BigDecimal("1654400"))
                .build();
        sales.addSalesItem(salesItem);

        log.info(" ### salesItem 생성 및 Sales에 추가");

        SalesItem salesItem1 = SalesItem.builder()
                .itemName("테스트 품목2")
                .quantity(1L)
                .unitPrice(new BigDecimal("1504000"))
                .vat(new BigDecimal("15040"))
                .totalAmount(new BigDecimal("1654400"))
                .build();
        sales.addSalesItem(salesItem1);

        log.info(" ### salesItem1 생성 및 Sales에 추가");

        SalesItem salesItem2 = SalesItem.builder()
                .itemName("테스트 품목3")
                .quantity(1L)
                .unitPrice(new BigDecimal("1504000"))
                .vat(new BigDecimal("15040"))
                .totalAmount(new BigDecimal("1654400"))
                .build();
        sales.addSalesItem(salesItem2);

        log.info(" ### salesItem2 생성 및 Sales에 추가");

        salesRepository.saveAndFlush(sales);
        log.info(" ### Sales 엔티티와 모든 SalesItem 저장 완료");

        Sales savedSales = salesRepository.findById("20251007-93").orElseThrow();
        log.info("저장된 SalesItem 개수: {}", savedSales.getSalesItems().size());
    }
}
