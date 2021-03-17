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
import java.util.Optional;

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

	@Scheduled(cron = "0 5 2 * * *", zone = "Asia/Seoul") // 매일 02 시 05 분 실행
	//	@Scheduled(fixedRate = 1000000000) // 테스트용
	public List<Stocks> getStockList() throws Exception {
		LocalDate now = LocalDate.now();

		List<StocksPrice> stockPriceList = new ArrayList<>();

		List<Stocks> stockList = stockRepository.findAllByMarket("KS")
			.orElseThrow(() -> new Exception("종목이 조회되지 않습니다."));

		//		stockList = stockList.subList(0, 10); // 10개만 테스트

		for (Stocks target : stockList) {
			StocksPrice stocksPrice = Optional.ofNullable(target.getStocksPrice()).orElse(new StocksPrice());

			String stockCode = target.getCode() + "." + target.getMarket();

			Stock stock = YahooFinance.get(stockCode, true);

			BigDecimal price = stock.getQuote().getPrice();
			stocksPrice.setStocksId(target.getId());
			stocksPrice.setPrice(price);
			Calendar dateCheck = stock.getHistory().get(stock.getHistory().size() - 1).getDate();
			stocksPrice.setDateCheck(convertDateTime(dateCheck));

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
				log.error("{} / {} year fail", stockCode, 1);
				continue;
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
				log.error("{} / {} year fail", stockCode, 3);
				continue;
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
				log.error("{} / {} year fail", stockCode, 5);
				continue;
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
				log.error("{} / {} year fail", stockCode, 10);
				continue;
			}

			stockPriceList.add(stocksPrice);
		}

		stocksPriceRepository.saveAll(stockPriceList);

		return null;
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
