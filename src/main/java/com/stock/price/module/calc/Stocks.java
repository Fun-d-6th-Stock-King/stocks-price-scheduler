package com.stock.price.module.calc;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stocks")
@NoArgsConstructor
@Data
public class Stocks implements Serializable {

    private static final long serialVersionUID = 6426956270036697407L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "company")
    private String company;

    @Column(name = "code")
    private String code;

    @Column(name = "sector")
    private String sector;

    @Column(name = "product")
    private String product;

    @Column(name = "listing_date")
    private String listingDate;

    @Column(name = "settle_month")
    private String settleMonth;

    @Column(name = "representative")
    private String representative;

    @Column(name = "homepage")
    private String homepage;

    @Column(name = "area")
    private String area;

    @Column(name = "market")
    private String market;

}
