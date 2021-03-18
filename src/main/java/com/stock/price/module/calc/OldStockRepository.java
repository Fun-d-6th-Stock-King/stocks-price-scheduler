package com.stock.price.module.calc;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OldStockRepository extends JpaRepository<OldStock, Integer> {
    public Optional<OldStock> findByStocksId(int stocksId);
}
