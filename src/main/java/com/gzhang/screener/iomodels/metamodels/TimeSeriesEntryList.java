package com.gzhang.screener.iomodels.metamodels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesEntryList {
    List<TimeSeriesEntry> timeSeriesEntries;
}
