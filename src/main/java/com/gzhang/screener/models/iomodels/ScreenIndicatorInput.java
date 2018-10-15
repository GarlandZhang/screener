package com.gzhang.screener.models.iomodels;

import com.gzhang.screener.models.ScreenIndicator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScreenIndicatorInput {
    float parameterPercentChange;
    String parameterTimeInterval;
    boolean parameterDirection;

    public ScreenIndicatorInput(ScreenIndicator screenIndicator) {
        parameterPercentChange = screenIndicator.getParameterPercentChange();
        parameterTimeInterval = screenIndicator.getParameterTimeInterval();
        parameterDirection = screenIndicator.isParameterDirection();
    }

    public ScreenIndicator toScreenIndicator() {
        ScreenIndicator screenIndicator = new ScreenIndicator();
        screenIndicator.setParameterTimeInterval(parameterTimeInterval);
        screenIndicator.setParameterPercentChange(parameterPercentChange);
        screenIndicator.setParameterDirection(parameterDirection);
        return screenIndicator;
    }
}
