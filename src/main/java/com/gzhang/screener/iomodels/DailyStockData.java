package com.gzhang.screener.iomodels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigInteger;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DailyStockData {
    int id;
    int metadataId;
    Date dateCreated;
    double openPrice;
    double highPrice;
    double lowPrice;
    double closePrice;
    long volume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="metadataId", insertable = false, updatable = false)
    StockMetadata stockMetadata;
}
