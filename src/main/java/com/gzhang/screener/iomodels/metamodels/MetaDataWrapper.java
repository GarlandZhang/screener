package com.gzhang.screener.iomodels.metamodels;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataWrapper {
    Map<String, String> metaDataList;
}
