package com.gzhang.screener.repositories;

import com.gzhang.screener.models.ScreenIndicatorGrouping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ScreenIndicatorGroupingRepositoryImpl implements ScreenIndicatorGroupingRepository{

    @Autowired
    ScreenIndicatorGroupingJpaRepository screenIndicatorGroupingJpaRepository;

    @Override
    public ScreenIndicatorGrouping save(ScreenIndicatorGrouping screenIndicatorGrouping) {
        return screenIndicatorGroupingJpaRepository.save(screenIndicatorGrouping);
    }
}
