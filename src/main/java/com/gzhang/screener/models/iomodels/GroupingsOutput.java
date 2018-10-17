package com.gzhang.screener.models.iomodels;

import com.gzhang.screener.models.ScreenIndicatorGrouping;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GroupingsOutput {
    List<ScreenIndicatorGroupingOutput> groupingOutputList;

    public GroupingsOutput() {
        groupingOutputList = new ArrayList<>();
    }

    public GroupingsOutput(List<ScreenIndicatorGrouping> groupings) {
        this();
        for(ScreenIndicatorGrouping grouping: groupings) {
            groupingOutputList.add(new ScreenIndicatorGroupingOutput(grouping));
        }
    }
}
