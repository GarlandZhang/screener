package com.gzhang.screener.models.metamodels;

import com.gzhang.screener.models.StockMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
