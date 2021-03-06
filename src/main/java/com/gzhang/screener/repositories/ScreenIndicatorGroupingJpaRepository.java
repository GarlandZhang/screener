package com.gzhang.screener.repositories;

import com.gzhang.screener.models.ScreenIndicator;
import com.gzhang.screener.models.ScreenIndicatorGrouping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreenIndicatorGroupingJpaRepository extends JpaRepository<ScreenIndicatorGrouping, Integer> {
    ScreenIndicatorGrouping findScreenIndicatorGroupingById(int id);
}
