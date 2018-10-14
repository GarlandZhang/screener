package com.gzhang.screener.iomodels.metamodels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataPair {
    String metaDataKey;
    String metaDataValue;
}
