package com.bizmate.salesPages.management.order.order.domain;

import com.bizmate.salesPages.management.order.orderItem.domain.OrderItem;
import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@Entity
@Table(name = "ORDER_MASTER")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    @Id
    @Column(length = 20)
    private String orderId;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    private LocalDate orderDate;

    private String projectId;
    private String projectName;
    private LocalDate orderDueDate;
    private BigDecimal orderAmount;
    private String userId;
    private String writer;
    private String clientId;
    private String clientCompany;
    private String orderNote;

    @Builder.Default
    private String orderStatus = "시작전";

    @Builder.Default
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem){
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }


    public void changeProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void changeProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void changeOrderDueDate(LocalDate orderDueDate) {
        this.orderDueDate = orderDueDate;
    }

    public void changeOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public void changeUserId(String userId) {
        this.userId = userId;
    }

    public void changeWriter(String writer) {
        this.writer = writer;
    }

    public void changeClientId(String clientId) {
        this.clientId = clientId;
    }

    public void changeClientCompany(String clientCompany) {
        this.clientCompany = clientCompany;
    }

    public void changeOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }
}
