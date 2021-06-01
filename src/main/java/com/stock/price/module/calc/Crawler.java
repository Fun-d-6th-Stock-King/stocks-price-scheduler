package com.stock.price.module.calc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {


    public static void getExceptionCaseStocks() throws Exception {
        // 거래 중지 종목
        String stoppedTradingURL = "https://finance.naver.com/sise/trading_halt.nhn?sosok=0";
        List<String> stoppedTradingCodes = getCodesFromNaver(stoppedTradingURL);

        // 시장 경보 종목
        String warnedURL = "https://finance.naver.com/sise/investment_alert.nhn?type=caution&sosok=0";
        List<String> warnedCodes = getCodesFromNaver(warnedURL);

        // 관리 종목
        String managedURL = "https://finance.naver.com/sise/management.nhn?sosok=0";
        List<String> managedCodes = getCodesFromNaver(managedURL);
        System.out.println("거래 중지: " + stoppedTradingCodes);
        System.out.println("시장 경보: " + warnedCodes);
        System.out.println("관리: " + managedCodes);

    }

    public static List<String> getCodesFromNaver(String url) throws Exception {
        try {
            // document 파싱
            Document doc = Jsoup.connect(url).get();
            List<Element> titles = doc.getElementsByClass("tltle");

            // 종목 코드 파싱
            List<String> stoppedCodes = new ArrayList<String>();
            for (Element title: titles) {
                String code = "";
                String href = title.attr("href");
                Matcher matcher = Pattern.compile("(\\d{6})$")
                        .matcher(href);
                while (matcher.find()) {
                    code += matcher.group();
                }
                stoppedCodes.add(code);
            }

            return stoppedCodes;

        } catch (IOException e) {
            throw e;
        }
    }
}
