package com.gzhang.screener.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DailyStockData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int metadataId;
    Date dateCreated;
    float openPrice;
    float highPrice;
    float lowPrice;
    float closePrice;
    long volume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="metadataId", insertable = false, updatable = false)
    StockMetadata stockMetadata;
}
