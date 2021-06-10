package com.stock.price.module.calc;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.stock.price.module.calc.Crawler.ExCase;

import lombok.extern.slf4j.Slf4j;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

@Service
@Slf4j
public class CalcService {

	@Autowired
	private StocksPriceRepository stocksPriceRepository;
	
	@Scheduled(cron = "0 0 1 * * 1-5", zone = "Asia/Seoul") // 평일 01 시 00 분 실행
//	@Scheduled(fixedRate = 86400000) // 테스트용
	public void getStockList() throws Exception {
		LocalDate now = LocalDate.now();
		
		List<StocksPrice> stockPriceList = stocksPriceRepository.findAllByIdNotIn(List.of(804L))
		    .orElseThrow(() -> new Exception("종목이 조회되지 않습니다."));
		
		List<StocksPrice> newStockPirceList = new ArrayList<>();
		
		stockPriceList.forEach(vo -> vo.setStopTrading(true));

		for (int i = 0; i < stockPriceList.size(); i++) {
		    StocksPrice stocksPrice = stockPriceList.get(i);
		    log.info("CALL [" + stocksPrice.getCompany() + "] " + stocksPrice.getCode());
		    String stockCode = stocksPrice.getCode() + ".KS";
            try {
                Stock stock = YahooFinance.get(stockCode);
                BigDecimal price = stock.getQuote().getPrice();
                stocksPrice.setPrice(price);
                stocksPrice.setLastTradeDate(convertDateTime(stock.getQuote().getLastTradeTime()));
                newStockPirceList.add(getStockHist(stock, stocksPrice, price, now));    
            } catch (Exception e) {
                log.error("stock.getQuote fail [{}]", stocksPrice.getCompany());
                log.error("Exception", e);
            }
            Random randomGenerator = new Random();

            Thread.sleep(2000 + randomGenerator.nextInt(2000));
        }
		
		// [start] kospi
		StocksPrice stocksPrice = stocksPriceRepository.findById(804L).orElse(new StocksPrice());
        String stockCode = "^KS11"; // 005930.KS
        Stock stock = YahooFinance.get(stockCode);
        
        BigDecimal price = stock.getQuote().getPrice();
        stocksPrice.setPrice(price);
        stocksPrice.setLastTradeDate(convertDateTime(stock.getQuote().getLastTradeTime()));
        newStockPirceList.add(getStockHist(stock, stocksPrice, price, now));
        // [end] kospi
        
		stocksPriceRepository.saveAll(newStockPirceList); // 남은게 있으면 저장
		
		log.info("스케줄 종료");
	}
	
	/**
	 * 종목별 historical 데이터를 가져옴니다. 수익금, 수익률 계산도합니다.
	 * @param stock
	 * @param stocksPrice
	 * @param price
	 * @param now
	 * @return
	 */
	public StocksPrice getStockHist(Stock stock, StocksPrice stocksPrice, BigDecimal price, LocalDate now) {
	    
	    stocksPrice.setMarketCap(stock.getStats().getMarketCap() != null ? stock.getStats().getMarketCap() : new BigDecimal(0));
	    stocksPrice.setStopTrading(false);
	    
        // 1주일전
        try {
            List<HistoricalQuote> hist = getHistory(stock, now.minusWeeks(1), now.minusWeeks(1).plusDays(7),
                    Interval.DAILY);

            HistoricalQuote quote = hist.get(0);
            LocalDateTime date = convertDateTime(quote.getDate());
            BigDecimal oldPrice = quote.getClose();
            BigDecimal yield = price.subtract(oldPrice).divide(oldPrice, MathContext.DECIMAL32)
                    .multiply(new BigDecimal(10000));

            stocksPrice.setDateW1(date);
            stocksPrice.setPriceW1(oldPrice);
            stocksPrice.setYieldW1(yield);

        } catch (Exception e) {
            log.error("{} / {} Weeks fail", stock.getName(), 1);
            return stocksPrice;
        }
        
        // 한달전
        try {
            List<HistoricalQuote> hist = getHistory(stock, now.minusMonths(1), now.minusMonths(1).plusDays(7),
                    Interval.DAILY);

            HistoricalQuote quote = hist.get(0);
            LocalDateTime date = convertDateTime(quote.getDate());
            BigDecimal oldPrice = quote.getClose();
            BigDecimal yield = price.subtract(oldPrice).divide(oldPrice, MathContext.DECIMAL32)
                    .multiply(new BigDecimal(10000));

            stocksPrice.setDateM1(date);
            stocksPrice.setPriceM1(oldPrice);
            stocksPrice.setYieldM1(yield);

        } catch (Exception e) {
            log.error("{} / {} month fail", stock.getName(), 1);
            return stocksPrice;
        }
        
        // 6개월전
        try {
            List<HistoricalQuote> hist = getHistory(stock, now.minusMonths(6), now.minusMonths(6).plusDays(7),
                    Interval.DAILY);

            HistoricalQuote quote = hist.get(0);
            LocalDateTime date = convertDateTime(quote.getDate());
            BigDecimal oldPrice = quote.getClose();
            BigDecimal yield = price.subtract(oldPrice).divide(oldPrice, MathContext.DECIMAL32)
                    .multiply(new BigDecimal(10000));

            stocksPrice.setDateM6(date);
            stocksPrice.setPriceM6(oldPrice);
            stocksPrice.setYieldM6(yield);

        } catch (Exception e) {
            log.error("{} / {} Month fail", stock.getName(), 6);
            return stocksPrice;
        }

        // 1년전
        try {
            List<HistoricalQuote> hist = getHistory(stock, now.plusYears(-1), now.plusYears(-1).plusDays(7),
                Interval.DAILY);
            
            HistoricalQuote quote = hist.get(0);
            LocalDateTime date = convertDateTime(quote.getDate());
            BigDecimal oldPrice = quote.getClose();
            BigDecimal yield = price
                .subtract(oldPrice)
                .divide(oldPrice, MathContext.DECIMAL32)
                .multiply(new BigDecimal(10000));
            
            stocksPrice.setDateY1(date);
            stocksPrice.setPriceY1(oldPrice);
            stocksPrice.setYieldY1(yield);
        } catch (Exception e) {
            log.error("{} / {} year fail", stock.getName(), 1);
            return stocksPrice;
        }
        
        // 3년전
        try {
            List<HistoricalQuote> hist = getHistory(stock, now.plusYears(-3), now.plusYears(-3).plusDays(7),
                Interval.DAILY);
            
            HistoricalQuote quote = hist.get(0);
            LocalDateTime date = convertDateTime(quote.getDate());
            BigDecimal oldPrice = quote.getClose();
            BigDecimal yield = price
                .subtract(oldPrice)
                .divide(oldPrice, MathContext.DECIMAL32)
                .multiply(new BigDecimal(10000));
            
            stocksPrice.setDateY3(date);
            stocksPrice.setPriceY3(oldPrice);
            stocksPrice.setYieldY3(yield);
        } catch (Exception e) {
            log.error("{} / {} year fail", stock.getName(), 3);
            return stocksPrice;
        }
        
        // 5년전 
        try {
            List<HistoricalQuote> hist = getHistory(stock, now.plusYears(-5), now.plusYears(-5).plusDays(7),
                Interval.DAILY);
            
            HistoricalQuote quote = hist.get(0);
            LocalDateTime date = convertDateTime(quote.getDate());
            BigDecimal oldPrice = quote.getClose();
            BigDecimal yield = price
                .subtract(oldPrice)
                .divide(oldPrice, MathContext.DECIMAL32)
                .multiply(new BigDecimal(10000));
            
            stocksPrice.setDateY5(date);
            stocksPrice.setPriceY5(oldPrice);
            stocksPrice.setYieldY5(yield);
        } catch (Exception e) {
            log.error("{} / {} year fail", stock.getName(), 5);
            return stocksPrice;
        }
        
        // 10년전 
        try {
            List<HistoricalQuote> hist = getHistory(stock, now.plusYears(-10), now.plusYears(-10).plusDays(7),
                Interval.DAILY);
            
            HistoricalQuote quote = hist.get(0);
            LocalDateTime date = convertDateTime(quote.getDate());
            BigDecimal oldPrice = quote.getClose();
            BigDecimal yield = price
                .subtract(oldPrice)
                .divide(oldPrice, MathContext.DECIMAL32)
                .multiply(new BigDecimal(10000));
            
            stocksPrice.setDateY10(date);
            stocksPrice.setPriceY10(oldPrice);
            stocksPrice.setYieldY10(yield);
        } catch (Exception e) {
            log.error("{} / {} year fail", stock.getName(), 10);
            return stocksPrice;
        }
	    
        return stocksPrice;
	}
	

	/**
	 * LocalDate 객체를 Calendar 객체로 변화함
	 * @param localDate
	 * @return
	 */
	public static Calendar convertCal(LocalDate localDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
		return calendar;
	}

	/**
	 * Calendar 객체를 LocalDateTime 객체로 변화함
	 * @param calendar
	 * @return
	 */
	public static LocalDateTime convertDateTime(Calendar calendar) {
		return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.of("Asia/Seoul"));
	}

	/**
	 * LocalDate 객체를 사용하여 histortical 데이터를 가져옵니다
	 * @param stock
	 * @param from
	 * @param to
	 * @param interval
	 * @return
	 * @throws IOException
	 */
	public static List<HistoricalQuote> getHistory(Stock stock, LocalDate from, LocalDate to, Interval interval)
		throws IOException {
		return stock.getHistory(convertCal(from), convertCal(to), interval);
	}

     @Scheduled(cron = "0 5 7-18 * * 1-5", zone = "Asia/Seoul") // 평일 7 ~ 18 시 사이에 매 05 분 실행
//     @Scheduled(fixedRate = 86400000) // 테스트용
     public void updateExceptionCaseStocks() throws Exception {
         log.info("거래중지 스케줄러 시작");
         List<StocksPrice> stockPriceList = stocksPriceRepository.findAllByIdNotIn(List.of(804L))
                 .orElseThrow(() -> new Exception("종목이 조회되지 않습니다."));
         
         ExCase exCase = new Crawler().getExceptionCaseStocks();
         
         stockPriceList.forEach(vo -> {
             vo.setTradingHalt(false);
             vo.setManagement(false);
             vo.setInvestmentAlert(false);
             
             if(exCase.getStoppedTradingCodes().contains(vo.getCode())) {
                 vo.setTradingHalt(true);
             }
             
             if(exCase.getManagedCodes().contains(vo.getCode())) {
                 vo.setManagement(true);
             }
             
             if(exCase.getWarnedCodes().contains(vo.getCode())) {
                 vo.setInvestmentAlert(true);
             }
             
         });
         
         stocksPriceRepository.saveAll(stockPriceList);

         log.info("거래중지 스케줄러 종료");
     }

}
