package com.bizmate.salesPage.report.salesTarget;

import com.bizmate.salesPages.report.salesTarget.domain.SalesTarget;
import com.bizmate.salesPages.report.salesTarget.repository.SalesTargetRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

@SpringBootTest
@Slf4j
public class SalesTargetRepositoryTests {
    @Autowired
    private SalesTargetRepository salesTargetRepository;

    @Test
    public void testInsert(){
        for(int i =1; i <= 10; i++){
            SalesTarget salesTarget = SalesTarget.builder()
                    .targetAmount(new BigDecimal(i + "1000000"))
                    .targetMonth(i)
                    .targetYear(2025)
                    .build();
            salesTargetRepository.save(salesTarget);
            log.info("목표 매출 등록 완료");
        }
    }

    @Test
    public void testRead() {
        Long targetId = 9L;

        Optional<SalesTarget> result = salesTargetRepository.findById(targetId);
        SalesTarget salesTarget = result.orElseThrow();
        log.info("데이터 조회(9L): {}", salesTarget);
    }
}
