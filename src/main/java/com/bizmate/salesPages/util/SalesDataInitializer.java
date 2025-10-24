package com.bizmate.salesPages.util;

import com.bizmate.salesPages.client.domain.Client;
import com.bizmate.salesPages.client.repository.ClientRepository;
import com.bizmate.salesPages.management.collections.domain.Collection;
import com.bizmate.salesPages.management.collections.repository.CollectionRepository;
import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.order.order.repository.OrderRepository;
import com.bizmate.salesPages.management.order.orderItem.domain.OrderItem;
import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.bizmate.salesPages.management.sales.sales.repository.SalesRepository;
import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;
import com.bizmate.salesPages.report.salesTarget.domain.SalesTarget;
import com.bizmate.salesPages.report.salesTarget.repository.SalesTargetRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class SalesDataInitializer implements CommandLineRunner {

    // =========================
    //  Open API (ODcloud) 설정
    // =========================
    // Base URL: https://api.odcloud.kr/api
    // Namespace: /15008777/v1
    // Dataset (종로구 사회적기업): /uddi:0b0a4577-9838-4b4a-8570-d0a8e65f45fd
    private static final String ODCLOUD_BASE = "https://api.odcloud.kr/api";
    private static final String ODCLOUD_PATH = "/15008777/v1/uddi:0b0a4577-9838-4b4a-8570-d0a8e65f45fd";
    // 사용자 제공 키 (일반 인증키)
    private static final String ODCLOUD_SERVICE_KEY_RAW =
            "1c0c79de6d47d284295f30883d19bf6503aaf8a428e4cab1dbed2e11e287d598";
    private static final String ODCLOUD_SERVICE_KEY =
            URLEncoder.encode(ODCLOUD_SERVICE_KEY_RAW, StandardCharsets.UTF_8);

    // =========================
    // Repository & Infra
    // =========================
    private final ClientRepository clientRepository;
    private final SalesTargetRepository salesTargetRepository;
    private final OrderRepository orderRepository;
    private final SalesRepository salesRepository;
    private final CollectionRepository collectionRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // =========================
    // 더미/공통
    // =========================
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final Random random = new Random();
    private final Map<LocalDate, Integer> orderSerialCounter = new HashMap<>();
    private final Map<LocalDate, Integer> salesSerialCounter = new HashMap<>();
    private final Map<LocalDate, Integer> collectionSerialCounter = new HashMap<>();

    @Override
    @Transactional
    public void run(String... args) {
        log.info("▶▶▶ Sales DataInitializer 실행 시작");

        initSalesTargets();      // 1) 목표 (독립)
        initClientsFromODcloud(); // 2) 거래처 (ODcloud)
        initDummyData();         // 3) 주문/매출/수금 (거래처 의존)

        log.info("✅ Sales DataInitializer 실행 완료");
    }

    // =========================================================
    // 1. (API) 거래처 데이터 초기화 - ODcloud 종로구 사회적기업
    // =========================================================
    private void initClientsFromODcloud() {
        if (clientRepository.count() > 0) {
            log.info("🔹 [Client] 데이터가 이미 존재하여 API 호출을 건너뜁니다.");
            return;
        }
        log.info("▶ [Client] ODcloud(종로구 사회적기업) API를 통해 거래처 데이터 생성 시작...");

        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(ODCLOUD_BASE + ODCLOUD_PATH)
                    .queryParam("page", 1)
                    .queryParam("perPage", 100)      // 필요 시 더 크게
                    .queryParam("serviceKey", ODCLOUD_SERVICE_KEY)
                    .toUriString();

            log.info("▶ [Client] API 호출 시도: {}", url);

            // 1) Raw 로깅
            ResponseEntity<String> raw = restTemplate.getForEntity(url, String.class);
            log.info("✅ [Client] API 원본 응답 (Raw): {}", raw.getBody());

            // 2) DTO 매핑
            OdcloudResponse od = objectMapper.readValue(raw.getBody(), OdcloudResponse.class);

            if (od == null || od.data == null || od.data.isEmpty()) {
                log.warn("❕ [Client] ODcloud 데이터가 비어있습니다. 더미 거래처로 대체합니다.");
                createDummyClients(20);
                return;
            }

            // 3) 데이터 -> Client 매핑
            List<Client> toSave = new ArrayList<>();
            for (Map<String, Object> row : od.data) {
                String company = pick(row,
                        "기업명", "업체명", "상호", "법인명", "기관명", "회사명");
                String ceo = pick(row,
                        "대표자명", "대표자", "대표", "대표자 성명");
                String bizNo = pick(row,
                        "사업자등록번호", "사업자번호", "법인등록번호", "고유번호");
                String address = pick(row,
                        "소재지", "주소", "소재지주소", "사업장소재지", "기업주소");

                // 회사명 없으면 스킵
                if (isBlank(company)) continue;

                // 사업자번호 없으면 생성(더미)
                if (isBlank(bizNo)) {
                    bizNo = String.format("800-%02d-%05d",
                            random.nextInt(90) + 10, random.nextInt(90000) + 10000);
                }

                Client client = Client.builder()
                        .clientId(formatClientId(bizNo))
                        .clientCompany(company)
                        .clientCeo(defaultIfBlank(ceo, "대표자미상"))
                        .clientBusinessType("사회적기업") // 데이터셋 성격상 고정 라벨
                        .clientAddress(defaultIfBlank(address, "서울 종로구 (미상)"))
                        .clientContact(generateRandomPhone())
                        .clientEmail("odcloud@" + company.replaceAll("\\s+", "").toLowerCase() + ".com")
                        .writer("SYSTEM")
                        .userId("admin")
                        .validationStatus(true)
                        .build();
                toSave.add(client);
            }

            if (toSave.isEmpty()) {
                log.warn("❕ [Client] 파싱된 유효 데이터가 없어 더미 데이터로 대체합니다.");
                createDummyClients(20);
            } else {
                clientRepository.saveAll(toSave);
                log.info("✅ [Client] ODcloud에서 {}건 저장 완료", toSave.size());
            }

        } catch (Exception e) {
            log.warn("⚠️ [Client] ODcloud 호출/파싱 실패 → 더미 데이터로 대체합니다. 원인: {}", e.getMessage(), e);
            createDummyClients(20);
        }
    }

    // =========================================================
    // 2. (Dummy) 매출 목표 데이터 초기화
    // =========================================================
    private void initSalesTargets() {
        if (salesTargetRepository.count() > 0) {
            log.info("🔹 [SalesTarget] 데이터가 이미 존재하여 더미 생성을 건너뜁니다.");
            return;
        }
        log.info("▶ [SalesTarget] 매출 목표 더미 데이터 생성 시작...");

        LocalDate today = LocalDate.now();
        List<SalesTarget> targets = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            if (salesTargetRepository.findByTargetYearAndTargetMonth(today.getYear(), month).isEmpty()) {
                SalesTarget target = SalesTarget.builder()
                        .targetYear(today.getYear())
                        .targetMonth(month)
                        .targetAmount(BigDecimal.valueOf(100_000_000L + random.nextInt(50_000_000)))
                        .writer("SYSTEM")
                        .userId("admin")
                        .build();
                targets.add(target);
            }
        }
        salesTargetRepository.saveAll(targets);
        log.info("✅ [SalesTarget] {}년도 매출 목표 {}건 생성 완료", today.getYear(), targets.size());
    }

    // =========================================================
    // 3. (Dummy) 주문/매출/수금 데이터 초기화
    // =========================================================
    private void initDummyData() {
        if (orderRepository.count() > 0 || salesRepository.count() > 0 || collectionRepository.count() > 0) {
            log.info("🔹 [Order/Sales/Collection] 데이터가 이미 존재하여 더미 생성을 건너뜁니다.");
            return;
        }

        List<Client> clients = clientRepository.findAll();
        if (clients.isEmpty()) {
            log.warn("❕ [Order/Sales/Collection] 참조할 거래처(Client)가 없어 생성을 건너뜁니다.");
            return;
        }

        log.info("▶ [Order/Sales/Collection] 더미 데이터 생성 시작...");

        List<Order> savedOrders = new ArrayList<>();
        List<Sales> savedSales = new ArrayList<>();
        List<Collection> savedCollections = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Client randomClient = clients.get(random.nextInt(clients.size()));
            LocalDate randomDate = LocalDate.now().minusDays(random.nextInt(365));

            String orderId = getNextIdForDate(orderSerialCounter, randomDate);
            Order order = Order.builder()
                    .orderId(orderId)
                    .orderIdDate(randomDate)
                    .orderDate(randomDate)
                    .orderDueDate(randomDate.plusDays(30))
                    .projectId("P" + (2024000 + i))
                    .projectName("더미 프로젝트 " + i)
                    .clientId(randomClient.getClientId())
                    .clientCompany(randomClient.getClientCompany())
                    .writer("SYSTEM")
                    .userId("admin")
                    .orderStatus("시작전")
                    .orderNote("자동 생성된 더미 주문")
                    .build();

            List<OrderItem> items = new ArrayList<>();
            for (int j = 0; j < random.nextInt(2) + 1; j++) {
                OrderItem item = OrderItem.builder()
                        .itemName("더미 품목 " + j)
                        .quantity(1L + random.nextInt(5))
                        .unitPrice(BigDecimal.valueOf(1_000_000 + random.nextInt(9_000_000)))
                        .lineNum(j + 1)
                        .build();
                item.calculateAmount();
                items.add(item);
            }
            order.updateOrderItems(items);
            order.calculateOrderAmount();
            savedOrders.add(order);

            if (random.nextInt(10) < 7) {
                LocalDate salesDate = randomDate.plusDays(random.nextInt(60));
                String salesId = getNextIdForDate(salesSerialCounter, salesDate);

                Sales sales = Sales.builder()
                        .salesId(salesId)
                        .salesIdDate(salesDate)
                        .salesDate(salesDate)
                        .deploymentDate(salesDate.plusDays(7))
                        .projectId(order.getProjectId())
                        .projectName(order.getProjectName())
                        .clientId(order.getClientId())
                        .clientCompany(order.getClientCompany())
                        .writer("SYSTEM")
                        .userId("admin")
                        .invoiceIssued(random.nextBoolean())
                        .order(order)
                        .build();

                List<SalesItem> salesItems = new ArrayList<>();
                for (OrderItem oi : order.getOrderItems()) {
                    SalesItem si = SalesItem.builder()
                            .itemName(oi.getItemName())
                            .quantity(oi.getQuantity())
                            .unitPrice(oi.getUnitPrice())
                            .unitVat(oi.getUnitVat())
                            .totalAmount(oi.getTotalAmount())
                            .lineNum(oi.getLineNum())
                            .build();
                    salesItems.add(si);
                }
                sales.updateSalesItems(salesItems);
                sales.calculateSalesAmount();
                savedSales.add(sales);

                if (sales.isInvoiceIssued() && sales.getSalesAmount().compareTo(order.getOrderAmount()) == 0) {
                    order.changeOrderStatus("완료");
                } else if (sales.isInvoiceIssued() && sales.getSalesAmount().compareTo(BigDecimal.ZERO) > 0) {
                    order.changeOrderStatus("진행중");
                }
            }

            if (random.nextInt(10) < 5) {
                LocalDate collectionDate = randomDate.plusDays(random.nextInt(90));
                String collectionId = getNextIdForDate(collectionSerialCounter, collectionDate);

                Collection collection = Collection.builder()
                        .collectionId(collectionId)
                        .collectionDate(collectionDate)
                        .collectionMoney(BigDecimal.valueOf(1_000_000 + random.nextInt(50_000_000)))
                        .collectionNote("더미 수금 데이터")
                        .writer("SYSTEM")
                        .userId("admin")
                        .client(randomClient)
                        .build();
                savedCollections.add(collection);
            }
        }

        orderRepository.saveAll(savedOrders);
        salesRepository.saveAll(savedSales);
        collectionRepository.saveAll(savedCollections);

        log.info("✅ [Order] {}건, [Sales] {}건, [Collection] {}건 더미 데이터 생성 완료",
                savedOrders.size(), savedSales.size(), savedCollections.size());
    }

    // =========================================================
    // 헬퍼 메서드
    // =========================================================
    private String formatClientId(String clientId) {
        if (clientId == null) return null;
        String rawId = clientId.replaceAll("[^0-9]", "");
        if (rawId.length() == 10) {
            return rawId.substring(0, 3) + "-" + rawId.substring(3, 5) + "-" + rawId.substring(5, 10);
        }
        return clientId;
    }

    private String generateRandomPhone() {
        return String.format("010-%04d-%04d",
                random.nextInt(9000) + 1000,
                random.nextInt(9000) + 1000);
    }

    private String getNextIdForDate(Map<LocalDate, Integer> counterMap, LocalDate date) {
        int nextSerial = counterMap.getOrDefault(date, 0) + 1;
        counterMap.put(date, nextSerial);
        return date.format(DATE_FORMAT) + "-" + String.format("%04d", nextSerial);
    }

    private static String pick(Map<String, Object> map, String... keys) {
        for (String k : keys) {
            Object v = map.get(k);
            if (v != null) {
                String s = String.valueOf(v).trim();
                if (!s.isEmpty() && !"null".equalsIgnoreCase(s)) return s;
            }
        }
        return null;
    }

    private static String defaultIfBlank(String s, String def) {
        return (s == null || s.trim().isEmpty()) ? def : s;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void createDummyClients(int count) {
        List<Client> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Client c = Client.builder()
                    .clientId(String.format("900-%02d-%05d", i, 10000 + i))
                    .clientCompany("더미 거래처 " + i)
                    .clientCeo("홍길동" + i)
                    .clientBusinessType("IT서비스")
                    .clientAddress("서울 종로구 (더미) " + i)
                    .clientContact(generateRandomPhone())
                    .clientEmail("dummy" + i + "@bizmate.com")
                    .writer("SYSTEM")
                    .userId("admin")
                    .validationStatus(true)
                    .build();
            list.add(c);
        }
        clientRepository.saveAll(list);
        log.info("✅ [Client] 더미 거래처 {}건 생성 완료", count);
    }

    // =========================================================
    // ODcloud 응답 DTO
    // =========================================================
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OdcloudResponse {
        @JsonProperty("currentCount")
        public Integer currentCount;

        @JsonProperty("data")
        public List<Map<String, Object>> data;

        @JsonProperty("matchCount")
        public Integer matchCount;

        @JsonProperty("page")
        public Integer page;

        @JsonProperty("perPage")
        public Integer perPage;

        @JsonProperty("totalCount")
        public Integer totalCount;
    }
}
