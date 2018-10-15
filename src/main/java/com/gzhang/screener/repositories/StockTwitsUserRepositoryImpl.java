package com.gzhang.screener.repositories;

import com.gzhang.screener.models.StockTwitsUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class StockTwitsUserRepositoryImpl implements StockTwitsUserRepository{

    @Autowired
    StockTwitsUserJpaRepository stockTwitsUserJpaRepository;

    @Override
    public StockTwitsUser save(StockTwitsUser stockTwitsUser) {
        return stockTwitsUserJpaRepository.save(stockTwitsUser);
    }
}
