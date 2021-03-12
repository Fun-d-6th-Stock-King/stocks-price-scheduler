package com.stock.price.module.calc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "no1.stocks_price")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StocksPrice implements Serializable {

	private static final long serialVersionUID = -1293146290479381252L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "stocks_id")
	private int stocksId;

	@Column(name = "price")
	private BigDecimal price;
	
	@Column(name = "date_check")
	private LocalDateTime dateCheck;
	
	@Column(name = "price_y1")
	private BigDecimal priceY1;
	
	@Column(name = "date_y1")
	private LocalDateTime dateY1;
	
	@Column(name = "yield_y1")
	private BigDecimal yieldY1;
}
