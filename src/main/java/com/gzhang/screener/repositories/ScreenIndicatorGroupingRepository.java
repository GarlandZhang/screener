package com.gzhang.screener.repositories;

import com.gzhang.screener.models.ScreenIndicatorGrouping;

public interface ScreenIndicatorGroupingRepository {

    ScreenIndicatorGrouping save(ScreenIndicatorGrouping screenIndicatorGrouping);

    ScreenIndicatorGrouping getById(int groupId);

    void delete(ScreenIndicatorGrouping grouping);

    void deleteById(int id);
}
