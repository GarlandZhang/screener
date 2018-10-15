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
public class ScreenIndicatorGroupingInput {
    List<ScreenIndicatorInput> screenIndicatorInputList;

    public ScreenIndicatorGroupingInput() {
        screenIndicatorInputList = new ArrayList<>();
    }

    public ScreenIndicatorGroupingInput(ScreenIndicatorGrouping screenIndicatorGrouping) {
        this();
        for(ScreenIndicator screenIndicator : screenIndicatorGrouping.getScreenIndicatorList()) {
            screenIndicatorInputList.add(new ScreenIndicatorInput(screenIndicator));
        }
    }

    public ScreenIndicatorGrouping toGrouping() {
        ScreenIndicatorGrouping screenIndicatorGrouping = new ScreenIndicatorGrouping();
        screenIndicatorGrouping.setScreenIndicatorList(new ArrayList<>());

        for(ScreenIndicatorInput screenIndicatorInput : screenIndicatorInputList) {
            screenIndicatorGrouping.addIndicator(screenIndicatorInput.toScreenIndicator());
        }

        return screenIndicatorGrouping;
    }
}
