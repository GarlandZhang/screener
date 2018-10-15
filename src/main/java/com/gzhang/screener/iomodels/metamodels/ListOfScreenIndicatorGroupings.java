package com.gzhang.screener.iomodels.metamodels;

import com.gzhang.screener.iomodels.ScreenIndicatorGrouping;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListOfScreenIndicatorGroupings {
    List<ScreenIndicatorGrouping> screenIndicatorGroupingList;
}
