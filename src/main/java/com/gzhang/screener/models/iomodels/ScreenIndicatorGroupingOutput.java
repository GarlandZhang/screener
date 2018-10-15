package com.gzhang.screener.models.iomodels;

import com.gzhang.screener.models.ScreenIndicator;
import com.gzhang.screener.models.ScreenIndicatorGrouping;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ScreenIndicatorGroupingOutput {
    int groupId;
    int userId;
    List<ScreenIndicatorOutput> screenIndicatorOutputList;

    public ScreenIndicatorGroupingOutput() {
        screenIndicatorOutputList = new ArrayList<>();
    }

    public ScreenIndicatorGroupingOutput(ScreenIndicatorGrouping screenIndicatorGrouping) {
        this();
        groupId = screenIndicatorGrouping.getId();
        userId = screenIndicatorGrouping.getUserId();
        for(ScreenIndicator screenIndicator: screenIndicatorGrouping.getScreenIndicatorList()) {
            screenIndicatorOutputList.add(new ScreenIndicatorOutput(screenIndicator));
        }
    }
}
