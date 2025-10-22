package com.bizmate.salesPages.management.sales.sales.domain;

import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@Builder
public class Sales {
    @Id
    @Column(length = 20)
    private String salesId;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    private LocalDate salesDate;

    private String projectId;
    private String projectName;
    private LocalDate deploymentDate;
    private BigDecimal salesAmount;
    private String userId;
    private String writer;
    private String clientId;
    private String clientCompany;
    private String salesNote;

    private String invoiceId;
    private boolean invoiceIssued;

    @Builder.Default
    @OneToMany(
            mappedBy = "sales",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SalesItem> salesItems = new ArrayList<>();

    public void addSalesItem(SalesItem salesItem){
        this.salesItems.add(salesItem);
        salesItem.setSales(this);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;


    public void changeProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void changeProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void changeDeploymentDate(LocalDate deploymentDate) {
        this.deploymentDate = deploymentDate;
    }

    public void changeSalesAmount(BigDecimal salesAmount) {
        this.salesAmount = salesAmount;
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

    public void changeSalesNote(String salesNote) {
        this.salesNote = salesNote;
    }
}
