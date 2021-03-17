package com.stock.price.module.calc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stocks_price")
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

	@Column(name = "price_y3")
	private BigDecimal priceY3;

	@Column(name = "date_y3")
	private LocalDateTime dateY3;

	@Column(name = "yield_y3")
	private BigDecimal yieldY3;

	@Column(name = "price_y5")
	private BigDecimal priceY5;

	@Column(name = "date_y5")
	private LocalDateTime dateY5;

	@Column(name = "yield_y5")
	private BigDecimal yieldY5;

	@Column(name = "price_y10")
	private BigDecimal priceY10;

	@Column(name = "date_y10")
	private LocalDateTime dateY10;

	@Column(name = "yield_y10")
	private BigDecimal yieldY10;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "stocks_id", insertable = false, updatable = false)
	private Stocks stocks;
}
