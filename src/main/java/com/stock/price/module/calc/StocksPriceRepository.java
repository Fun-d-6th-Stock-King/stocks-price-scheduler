package com.stock.price.module.calc;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StocksPriceRepository extends JpaRepository<StocksPrice, Long> {

    public Optional<StocksPrice> findByStocksId(Long stocksId);
    
    public Optional<List<StocksPrice>> findAllByIdNotIn(List<Long> ids);
}
