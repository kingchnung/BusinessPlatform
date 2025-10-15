package com.bizmate.salesPages.management.sales.sales.service;

import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.salesPages.common.dto.PageRequestDTO;
import com.bizmate.salesPages.common.dto.PageResponseDTO;
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
import java.util.ArrayList;
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

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDTO userDTO){
            salesDTO.setUserId(userDTO.getUsername());
            salesDTO.setWriter(userDTO.getEmpName());
        } else {
            throw new IllegalStateException("주문 등록을 위한 사용자 인증 정보를 찾을 수 없습니다. (비정상 접근)");
        }

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

        List<SalesItem> finalSalesItems;

        if (salesDTO.getSalesItems() != null && !salesDTO.getSalesItems().isEmpty()){
            finalSalesItems = salesDTO.getSalesItems().stream()
                    .map(itemDTO -> modelMapper.map(itemDTO, SalesItem.class))
                    .collect(Collectors.toList());
            }
        else if(order != null){
            finalSalesItems = new ArrayList<>();
            for(OrderItem orderItem : order.getOrderItems()){
                SalesItem salesItem = SalesItem.builder()
                        .itemName(orderItem.getItemName())
                        .quantity(orderItem.getQuantity())
                        .unitPrice(orderItem.getUnitPrice())
                        .unitVat(orderItem.getUnitVat())
                        .totalAmount(orderItem.getTotalAmount())
                        .itemNote(orderItem.getItemNote())
                        .lineNum(orderItem.getLineNum())
                        .build();
                finalSalesItems.add(salesItem);
            }
        }
         else {
             finalSalesItems = new ArrayList<>();
        }
        sales.updateSalesItems(finalSalesItems);

        sales.calculateSalesAmount();

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

        sales.changeClientId(salesDTO.getClientId());
        sales.changeDeploymentDate(salesDTO.getDeploymentDate());
        sales.changeSalesNote(salesDTO.getSalesNote());
        sales.changeProjectId(salesDTO.getProjectId());

        List<SalesItemDTO> newItemDto = salesDTO.getSalesItems();
        List<SalesItem> mergedItem = new ArrayList<>();

        for(SalesItemDTO itemDTO : newItemDto) {
            if(itemDTO.getSalesItemId() != null){
                SalesItem existingItem = sales.getSalesItems().stream()
                        .filter(item -> itemDTO.getSalesItemId().equals(item.getSalesItemId()))
                        .findFirst()
                        .orElse(null);

                if(existingItem != null) {
                    existingItem.changeItemName(itemDTO.getItemName());
                    existingItem.changeQuantity(itemDTO.getQuantity());
                    existingItem.changeUnitPrice(itemDTO.getUnitPrice());
                    existingItem.changeUnitVat(itemDTO.getUnitVat());
                    existingItem.changeItemNote(itemDTO.getItemNote());

                    existingItem.calculateAmount();
                    mergedItem.add(existingItem);
                }
            } else {
                SalesItem newItem = modelMapper.map(itemDTO, SalesItem.class);

                newItem.calculateAmount();
                mergedItem.add(newItem);
            }
        }

        sales.updateSalesItems(mergedItem);
        sales.calculateSalesAmount();
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
