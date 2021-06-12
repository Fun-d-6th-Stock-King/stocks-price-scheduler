package com.stock.price.module.calc;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
public class HistoryService {

    @Autowired
    private StocksPriceRepository stocksPriceRepository;
    
    @Autowired
    private StockHistoryRepository stockHistoryRepository;

    @Scheduled(fixedRate = 86400000) // 테스트용
    public void saveKospiHistory() throws Exception {
        
        LocalDate now = LocalDate.now();
        
        List<StocksPrice> stockPriceList = stocksPriceRepository.findAllByIdNotIn(List.of(804L))
            .orElseThrow(() -> new Exception("종목이 조회되지 않습니다."));
        
        for (int i = 0; i < stockPriceList.size(); i++) {
            
            try {
                Stock stock = YahooFinance.get(stockPriceList.get(i).getCode()+".KS");
                
                List<HistoricalQuote> hist = getHistory(stock, now.minusYears(20), now, Interval.DAILY);
                
                for (HistoricalQuote historicalQuote : hist) {
                    
                    Boolean isEmpty = stockHistoryRepository
                            .findByCodeAndDate(stockPriceList.get(i).getCode(), convertDateTime(historicalQuote.getDate()))
                            .isEmpty();
                    if(Boolean.TRUE.equals(isEmpty)) {
                        
                        try {
                            StockHistory stockHistory = StockHistory.builder()
                                    .code(stockPriceList.get(i).getCode())
                                    .adjClose(historicalQuote.getAdjClose())
                                    .date(convertDateTime(historicalQuote.getDate()))
                                    .close(historicalQuote.getClose())
                                    .high(historicalQuote.getHigh())
                                    .low(historicalQuote.getLow())
                                    .open(historicalQuote.getOpen())
                                    .symbol(historicalQuote.getSymbol())
                                    .volume(historicalQuote.getVolume())
                                    .build();
                            
                            stockHistoryRepository.save(stockHistory);
                        } catch (Exception e) {
                            log.error("입력 실패 == " + stockPriceList.get(i).getCompany(), e);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("조회 실패 == " + stockPriceList.get(i).getCompany(), e);
            }
        }
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
}
