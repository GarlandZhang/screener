package com.gzhang.screener.iomodels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StockMetadata {
    int id;
    String ticker;

    @OneToMany(mappedBy = "stockMetadata", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<DailyStockData> dailyStockDataList;
}
