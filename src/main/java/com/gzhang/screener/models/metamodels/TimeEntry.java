package com.gzhang.screener.models.metamodels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntry {
    Date date;
    float openPrice;
    float  highPrice;
    float lowPrice;
    float closePrice;
    long volume;
}
