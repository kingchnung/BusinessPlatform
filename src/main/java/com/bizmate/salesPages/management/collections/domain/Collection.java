package com.bizmate.salesPages.management.collections.domain;

import com.bizmate.salesPages.client.domain.Client;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Collection {
    @Id
    @Column(length = 20)
    private String collectionId;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    private LocalDate collectionDate;

    private BigDecimal collectionMoney;
    private String collectionNote;
    private String writer;
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_no", referencedColumnName = "clientNo",
            foreignKey = @ForeignKey(name = "FK_COLLECTION_CLIENT_NO"))
    private Client client;

    public void changeCollectionMoney(BigDecimal collectionMoney) {
        this.collectionMoney = collectionMoney;
    }

    public void changeCollectionNote(String collectionNote) {
        this.collectionNote = collectionNote;
    }

    public void changeClient(Client client) {
        this.client = client;
    }

    public void changeCollectionDate(LocalDate collectionDate) {
        this.collectionDate = collectionDate;
    }
}
