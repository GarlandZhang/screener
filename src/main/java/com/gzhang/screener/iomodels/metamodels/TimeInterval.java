package com.gzhang.screener.iomodels.metamodels;

public enum TimeInterval {
    LATEST(0),
    PAST_DAY(0),
    PAST_THREE_DAYS(172800000),
    PAST_WEEK(604800000),
    ;
/*    PAST_TWO_WEEKS,
    PAST_MONTH,
    PAST_QUARTER,
    PAST_SEMI_ANUALLY,
    YTD,
    YEARLY,
    FIVE_YEARS*/

    private long numMillis;

    TimeInterval(long i) {
        numMillis = i;
    }

    public long getNumMillis() {
        return numMillis;
    }
}
