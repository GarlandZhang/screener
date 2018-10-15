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
@NoArgsConstructor
@AllArgsConstructor
public class ScreenIndicatorGroupingInput {
    List<ScreenIndicatorInput> screenIndicatorInputList;

    public ScreenIndicatorGrouping toGrouping() {
        ScreenIndicatorGrouping screenIndicatorGrouping = new ScreenIndicatorGrouping();
        screenIndicatorGrouping.setScreenIndicatorList(new ArrayList<>());

        for(ScreenIndicatorInput screenIndicatorInput : screenIndicatorInputList) {
            screenIndicatorGrouping.addIndicator(screenIndicatorInput.toScreenIndicator());
        }
    }
}
