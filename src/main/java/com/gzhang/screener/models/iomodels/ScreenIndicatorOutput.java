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
public class ScreenIndicatorOutput {
    float parameterPercentChange;
    String parameterTimeInterval;
    boolean parameterDirection;

    public ScreenIndicatorOutput(ScreenIndicator screenIndicator) {
        parameterPercentChange = screenIndicator.getParameterPercentChange();
        parameterTimeInterval = screenIndicator.getParameterTimeInterval();
        parameterDirection = screenIndicator.isParameterDirection();
    }
}
