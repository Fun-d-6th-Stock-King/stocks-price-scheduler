package com.stock.price.module.calc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "old_stock")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
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

    @Column(name = "stocks_id")
    private int stocksId;
    
    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @Column(name = "one_day")
    private BigDecimal oneDay;
    
    @Column(name = "one_week")
    private BigDecimal oneWeek;
    
    @Column(name = "one_month")
    private BigDecimal oneMonth;
    
    @Column(name = "six_month")
    private BigDecimal sixMonth;
    
    @Column(name = "stop_trade")
    private Boolean stopTrade;
    
    @Column(name = "market_cap")
    private BigDecimal marketCap;
    
    @Column(name = "sector")
    private String sector;

    @OneToOne
    @JoinColumn(name = "stocks_id", insertable = false, updatable = false)
    private Stocks stocks;
}
