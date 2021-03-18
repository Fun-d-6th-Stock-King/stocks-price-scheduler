package com.stock.price.module.calc;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StocksPriceRepository extends JpaRepository<StocksPrice, Integer> {

    public Optional<StocksPrice> findByStocksId(int stocksId);
}
