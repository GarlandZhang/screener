package com.gzhang.screener.iomodels.metamodels;

import com.gzhang.screener.iomodels.StockMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SymbolList {
    List<String> symbols;

    public SymbolList() {
        symbols = new ArrayList<>();
    }

    public void add(StockMetadata stockMetadata) {
        symbols.add(stockMetadata.getTicker());
    }
}
