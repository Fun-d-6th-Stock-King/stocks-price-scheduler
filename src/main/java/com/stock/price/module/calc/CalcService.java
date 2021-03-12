package com.stock.price.module.calc;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

@Service
@Slf4j
public class CalcService {

	@Autowired
	private StocksRepository stockRepository;

	@Autowired
	private StocksPriceRepository stocksPriceRepository;

	//	@Scheduled(cron = "0 15 10 15 * ?", zone = "Asia/Seoul")
	@Scheduled(fixedRate = 10000000) // 테스트용
	public List<Stocks> getStockList() throws Exception {
		List<Stocks> stockList = stockRepository.findAllByMarket("KS")
			.orElseThrow(() -> new Exception("종목이 조회되지 않습니다."));

		Stocks target = stockList.get(0);

		String code = target.getCode() + "." + target.getMarket();

		log.info(code);

		LocalDate now = LocalDate.now();

		Stock stock = YahooFinance.get(code, true);

		BigDecimal price = stock.getQuote().getPrice();

		System.out.println(price);

		List<HistoricalQuote> hist = getHistory(stock, now.plusYears(-1), now.plusYears(-1).plusDays(7),
			Interval.DAILY);

		if (!hist.isEmpty()) {
			HistoricalQuote year1 = hist.get(0);
			LocalDateTime date = convertDateTime(year1.getDate());
			BigDecimal oldPrice = year1.getClose();
			BigDecimal yield = price
				.subtract(oldPrice)
				.divide(oldPrice, MathContext.DECIMAL32)
				.multiply(new BigDecimal(10000));
			System.out.println("1년 전 [" + date + "] " + oldPrice + " / " + yield);
			StocksPrice stocksPrice = stocksPriceRepository
				.findByStocksId(target.getId()).orElse(new StocksPrice());

			stocksPrice.setStocksId(target.getId());
			stocksPrice.setPrice(stock.getQuote().getPrice());
			Calendar dateCheck = stock.getHistory().get(stock.getHistory().size() - 1).getDate();
			stocksPrice.setDateCheck(convertDateTime(dateCheck));
			stocksPrice.setDateY1(date);
			stocksPrice.setPriceY1(oldPrice);
			stocksPrice.setYieldY1(yield);

			stocksPriceRepository.save(stocksPrice);
		}

		return stockList;
	}

	public static Calendar convertCal(LocalDate localDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
		return calendar;
	}

	public static LocalDateTime convertDateTime(Calendar calendar) {
		return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.of("Asia/Seoul"));
	}

	public static List<HistoricalQuote> getHistory(Stock stock, LocalDate from, LocalDate to, Interval interval)
		throws IOException {

		return stock.getHistory(convertCal(from), convertCal(to), interval);
	}
}
