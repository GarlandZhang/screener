package com.gzhang.screener.repositories;

import com.gzhang.screener.models.StockTwitsUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTwitsUserJpaRepository extends JpaRepository<StockTwitsUser, Integer> {


}
