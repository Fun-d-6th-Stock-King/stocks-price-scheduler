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

    @Column(name = "cur_price")
    private BigDecimal curPrice;
    
    @Column(name = "stocks_id")
    private int stocksId;
    
    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @OneToOne
    @JoinColumn(name = "stocks_id", insertable = false, updatable = false)
    private Stocks stocks;
}
