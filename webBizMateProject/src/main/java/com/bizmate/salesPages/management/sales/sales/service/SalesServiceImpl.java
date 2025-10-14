package com.bizmate.salesPages.management.sales.sales.service;

import com.bizmate.salesPages.common.dto.PageRequestDTO;
import com.bizmate.salesPages.common.dto.PageResponseDTO;
import com.bizmate.UserPrincipal;
import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.order.order.repository.OrderRepository;
import com.bizmate.salesPages.management.order.orderItem.domain.OrderItem;
import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.bizmate.salesPages.management.sales.sales.dto.SalesDTO;
import com.bizmate.salesPages.management.sales.sales.repository.SalesRepository;
import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;
import com.bizmate.salesPages.management.sales.salesItem.dto.SalesItemDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class SalesServiceImpl implements SalesService{
    private final SalesRepository salesRepository;
    private final OrderRepository orderRepository;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final ModelMapper modelMapper;

    @Override
    public String register(SalesDTO salesDTO) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String writerName = userPrincipal.getUsername();
        String writerId = userPrincipal.getUserId().toString();

        salesDTO.setUserId(writerId);
        salesDTO.setWriter(writerName);

        LocalDate today = LocalDate.now();

        salesDTO.setSalesDate(today);

        String maxSalesId = salesRepository.findMaxSalesIdBySalesDate(today).orElse(null);

        int nextSequence = 1;
        if(maxSalesId != null){
            try {
                String seqStr = maxSalesId.substring(9);
                nextSequence = Integer.parseInt(seqStr) + 1;
            } catch (Exception e) {
                nextSequence = 1;
            }
        }

        String datePart = today.format(DATE_FORMAT);
        String sequencePart = String.format("%04d", nextSequence);
        String finalSalesId = datePart + "-" + sequencePart;
        salesDTO.setSalesId(finalSalesId);

        Order order = null;
        if(salesDTO.getOrderId() != null && !salesDTO.getOrderId().isEmpty()){
            order = orderRepository.findById(salesDTO.getOrderId())
                    .orElseThrow(()->new NoSuchElementException("Order ID [" + salesDTO.getOrderId() + "]를 찾을 수 없습니다."));

            salesDTO.setProjectId(salesDTO.getProjectId() != null ? salesDTO.getProjectId() : order.getProjectId());
            salesDTO.setProjectName(salesDTO.getProjectName() != null? salesDTO.getProjectName() : order.getProjectName());
            salesDTO.setClientId(salesDTO.getClientId() != null? salesDTO.getClientId() : order.getClientId());
            salesDTO.setClientCompany(salesDTO.getClientCompany() != null? salesDTO.getClientCompany(): order.getClientCompany());
        }

        Sales sales = modelMapper.map(salesDTO, Sales.class);
        sales.setOrder(order);

        if(order != null && (salesDTO.getSalesItems() == null || salesDTO.getSalesItems().isEmpty())){
            for(OrderItem orderItem : order.getOrderItems()){
                SalesItem salesItem = SalesItem.builder()
                        .itemName(orderItem.getItemName())
                        .quantity(orderItem.getQuantity())
                        .unitPrice(orderItem.getUnitPrice())
                        .vat(orderItem.getVat())
                        .totalAmount(orderItem.getTotalAmount())
                        .itemNote(orderItem.getItemNote())
                        .lineNum(orderItem.getLineNum())
                        .build();
                sales.addSalesItem(salesItem);
            }
        }

        else if (salesDTO.getSalesItems() != null && !salesDTO.getSalesItems().isEmpty()){
            for(SalesItemDTO salesItemDTO : salesDTO.getSalesItems()){
                SalesItem salesItem = modelMapper.map(salesItemDTO, SalesItem.class);

                sales.addSalesItem(salesItem);
            }
        }

        Sales savedSales = salesRepository.save(sales);
        return savedSales.getSalesId();
    }

    @Override
    public SalesDTO get(String salesId) {
        Optional<Sales> result = salesRepository.findById(salesId);
        Sales sales = result.orElseThrow();
        SalesDTO dto = modelMapper.map(sales, SalesDTO.class);
        return dto;
    }

    @Override
    public void modify(SalesDTO salesDTO) {
        Optional<Sales> result = salesRepository.findById(salesDTO.getSalesId());
        Sales sales = result.orElseThrow(()->
                new NoSuchElementException("Sales ID ["+ salesDTO.getOrderId()+ "]을 찾을 수 없습니다."));

//        sales.changeSalesAmount(salesDTO.getSalesAmount());
        sales.changeDeploymentDate(salesDTO.getDeploymentDate());
        sales.changeClientCompany(salesDTO.getClientCompany());
        sales.changeSalesNote(salesDTO.getSalesNote());
        sales.changeProjectId(salesDTO.getProjectId());
        sales.changeProjectName(salesDTO.getProjectName());

        salesRepository.save(sales);
    }

    @Override
    public void remove(String salesId) {
        salesRepository.deleteById(salesId);
    }

    @Override
    public PageResponseDTO<SalesDTO> list(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() -1,
                pageRequestDTO.getSize(),
                Sort.by("salesId").descending());

        Page<Sales> result = salesRepository.findAll(pageable);
        List<SalesDTO> dtoList = result.getContent().stream().map(
                sales -> modelMapper.map(sales, SalesDTO.class)).collect(Collectors.toList());
        long totalCount = result.getTotalElements();

        PageResponseDTO<SalesDTO> responseDTO = PageResponseDTO.<SalesDTO>withAll().dtoList(dtoList).pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();

        return responseDTO;
    }
}
