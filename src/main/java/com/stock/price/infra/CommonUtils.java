package com.stock.price.infra;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class CommonUtils {

    private CommonUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * LocalDate 객체를 Calendar 객체로 변화함
     * 
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
     * 
     * @param calendar
     * @return
     */
    public static LocalDateTime convertDateTime(Calendar calendar) {
        return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.of("Asia/Seoul"));
    }

    /**
     * LocalDate 객체를 사용하여 histortical 데이터를 가져옵니다
     * 
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
