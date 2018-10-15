package com.gzhang.screener.models.metamodels;

import com.gzhang.screener.models.ScreenIndicatorGrouping;
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
