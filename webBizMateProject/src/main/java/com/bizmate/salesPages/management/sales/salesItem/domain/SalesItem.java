package com.bizmate.salesPages.management.sales.salesItem.domain;

import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "SALES_ITEM")
@SequenceGenerator(
        name = "SALES_ITEM_SEQ_GENERATOR",
        sequenceName = "SALES_ITEM_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class SalesItem {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SALES_ITEM_SEQ_GENERATOR"
    )
    private Long salesItemId;

    private String itemName;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal vat;
    private BigDecimal totalAmount;
    private String itemNote;
    private Integer lineNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id", nullable = false)
    @JsonBackReference
    private Sales sales;

    public void setSales(Sales sales){
        this.sales = sales;
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
