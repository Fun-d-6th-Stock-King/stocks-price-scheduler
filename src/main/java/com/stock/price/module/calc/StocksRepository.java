package com.stock.price.module.calc;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StocksRepository extends JpaRepository<Stocks, Integer> {
	
	public Optional<List<Stocks>> findAllByMarket(String market);

}
