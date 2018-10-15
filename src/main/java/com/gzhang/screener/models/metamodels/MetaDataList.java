package com.gzhang.screener.models.metamodels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataList {
//    List<MetaDataPair> metaDataPairs;
    MetaDataPair information;
    MetaDataPair symbol;
    MetaDataPair lastRefreshed;
    MetaDataPair outputSize;
    MetaDataPair timeZone;
}
