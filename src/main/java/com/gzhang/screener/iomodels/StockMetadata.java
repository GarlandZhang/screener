package com.gzhang.screener.iomodels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StockMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String ticker;

    @OneToMany(mappedBy = "stockMetadata", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<DailyStockData> dailyStockDataList;
}
