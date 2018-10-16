package com.gzhang.screener.models.metamodels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AlphavantageObject {
    MetaData metaData;
    List<TimeEntry> timeEntries;

    public AlphavantageObject() {
        metaData = new MetaData();
        timeEntries = new ArrayList<>();
    }
}
