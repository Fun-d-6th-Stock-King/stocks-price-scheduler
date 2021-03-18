package com.stock.price.module.calc;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "old_stock")
@NoArgsConstructor
@Data
public class OldStock implements Serializable {

    private static final long serialVersionUID = -4104616735360673972L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "company")
    private String company;

    @Column(name = "code")
    private String code;

    @Column(name = "one_year")
    private BigDecimal oneYear;

    @Column(name = "five_year")
    private BigDecimal fiveYear;

    @Column(name = "ten_year")
    private BigDecimal tenYear;

    @Column(name = "cur_price")
    private BigDecimal curPrice;
    
    @Column(name = "stocks_id")
    private int stocksId;

    @OneToOne
    @JoinColumn(name = "stocks_id", insertable = false, updatable = false)
    private Stocks stocks;
}
