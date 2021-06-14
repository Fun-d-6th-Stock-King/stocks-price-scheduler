package com.stock.price.module.calc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.stock.price.infra.CommonUtils;

import lombok.extern.slf4j.Slf4j;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

@Service
@Slf4j
public class HistoryService {

    @Autowired
    private StocksRepository stocksRepository;
    
    @Autowired
    private StockHistoryRepository stockHistoryRepository;

//    @Scheduled(fixedRate = 864000000) // 테스트용
    @Scheduled(cron = "0 0 19 * * 1-5", zone = "Asia/Seoul")  // 평일 19시에 실행
    public void saveKospiHistory() throws Exception {
        
        LocalDate now = LocalDate.now();
        
        List<Stocks> stockslist = stocksRepository.findAll();
        
        for (int i = 0; i < stockslist.size(); i++) {
            
            // 2021-06-11 자 데이터 있으면 지나감.
//            if(stockHistoryRepository
//                .findByCodeAndDate(stockslist.get(i).getCode(), LocalDateTime.parse("2021-06-14T00:00:00"))
//                .isPresent()) {
//                continue;
//            }
            
            try {
                Stock stock = YahooFinance.get(stockslist.get(i).getSymbol());
                
                List<HistoricalQuote> hist = CommonUtils.getHistory(stock, now.minusDays(2), now.plusDays(1), Interval.DAILY);  // 5일치 가져다가 없는거 입력
                
                for (HistoricalQuote historicalQuote : hist) {
                    
                    Boolean isEmpty = stockHistoryRepository
                            .findByCodeAndDate(stockslist.get(i).getCode(), CommonUtils.convertDateTime(historicalQuote.getDate()))
                            .isEmpty();
                    if(Boolean.TRUE.equals(isEmpty)) {
                        
                        try {
                            StockHistory stockHistory = StockHistory.builder()
                                    .code(stockslist.get(i).getCode())
                                    .adjClose(historicalQuote.getAdjClose())
                                    .date(CommonUtils.convertDateTime(historicalQuote.getDate()))
                                    .close(historicalQuote.getClose())
                                    .high(historicalQuote.getHigh())
                                    .low(historicalQuote.getLow())
                                    .open(historicalQuote.getOpen())
                                    .symbol(historicalQuote.getSymbol())
                                    .volume(historicalQuote.getVolume())
                                    .build();
                            
                            stockHistoryRepository.save(stockHistory);
                        } catch (Exception e) {
                            log.error("입력 실패 == " + stockslist.get(i).getCompany(), e);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("조회 실패 == " + stockslist.get(i).getCompany(), e);
            }
            
            Thread.sleep(300);
        }
    }

}
