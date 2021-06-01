package com.stock.price.module.calc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import lombok.Builder;
import lombok.Getter;

public class Crawler {

    public ExCase getExceptionCaseStocks() throws Exception {
        // 거래 중지 종목
        String stoppedTradingURL = "https://finance.naver.com/sise/trading_halt.nhn?sosok=0";
        List<String> stoppedTradingCodes = getCodesFromNaver(stoppedTradingURL);

        // 시장 경보 종목
        String warnedURL = "https://finance.naver.com/sise/investment_alert.nhn?type=caution&sosok=0";
        List<String> warnedCodes = getCodesFromNaver(warnedURL);

        // 관리 종목
        String managedURL = "https://finance.naver.com/sise/management.nhn?sosok=0";
        List<String> managedCodes = getCodesFromNaver(managedURL);
        
        return ExCase.builder()
                .stoppedTradingCodes(stoppedTradingCodes)
                .warnedCodes(warnedCodes)
                .managedCodes(managedCodes)
                .build();
    }
    
    @Builder
    @Getter
    public static class ExCase{
        private List<String> stoppedTradingCodes;
        private List<String> warnedCodes;
        private List<String> managedCodes;
    }

    public List<String> getCodesFromNaver(String url) throws Exception {
        // document 파싱
        Document doc = Jsoup.connect(url).get();
        List<Element> titles = doc.getElementsByClass("tltle");

        // 종목 코드 파싱
        List<String> stoppedCodes = new ArrayList<>();
        for (Element title : titles) {
            String href = title.attr("href");
            Matcher matcher = Pattern.compile("(\\d{6})$").matcher(href);
            while (matcher.find()) {
                stoppedCodes.add(matcher.group());
            }
        }
        return stoppedCodes;
    }
}
