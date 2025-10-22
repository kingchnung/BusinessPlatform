package com.bizmate.salesPages.management.order.orderItem.domain;

import com.bizmate.salesPages.management.order.order.domain.Order;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ORDER_ITEM")
@SequenceGenerator(
        name = "ORDER_ITEM_SEQ_GENERATOR",
        sequenceName = "ORDER_ITEM_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class OrderItem {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ORDER_ITEM_SEQ_GENERATOR"
    )
    private Long orderItemId;

    private String itemName;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal vat;
    private BigDecimal totalAmount;
    private String itemNote;
    private Integer lineNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    public void setOrder(Order order){
        this.order = order;
    }

    public void changeItemName(String itemName) {
        this.itemName = itemName;
    }

    public void changeQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void changeUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void changeVat(BigDecimal vat) {
        this.vat = vat;
    }

    public void changeTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void changeItemNote(String itemNote) {
        this.itemNote = itemNote;
    }

}
