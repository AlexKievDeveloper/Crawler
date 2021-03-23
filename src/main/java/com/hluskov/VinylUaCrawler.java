package com.hluskov;

import com.orlovkiy2.entity.Vinyl;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

@Slf4j
public class VinylUaCrawler extends WebCrawler {

    /**Сюда можно втулить условие когда не нужно посещать страницу*/
    private final Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz))$");

    private static int counter = 0;

    //private static List<Vinyl> vinyls = new CopyOnWriteArrayList<>();

    private static Set<Vinyl> vinyls = ConcurrentHashMap.newKeySet();


    /**Этот метод решает заходим ли мы на страницу или нет*/
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
                && href.startsWith("http://vinyl.ua/showcase/") || !FILTERS.matcher(href).matches()
                && href.startsWith("http://vinyl.ua/release/");
    }

    /**В этот метод заходим при посещении каждой странице которую should visit = true*/
    @SneakyThrows
    @Override
    public void visit(Page page) {
        log.info("Counter of visited page: {}", counter++);

        String url = page.getWebURL().getURL();
        log.info("Visiting page with URL: {}", url);
        log.info("Vinyls list size: {}", vinyls.size());

        /**Условие прекращение парсинга сайт*/
        if (url.equals("http://vinyl.ua/showcase/electronic?style=New%2BJack%2BSwing&page=4")) {
            myController.shutdown();
        }

        if (page.getParseData() instanceof HtmlParseData) {

            Document doc = Jsoup.connect(url).get();
            Elements pageElements = doc.getElementsByClass("vinyl-release showcase");

            for (Element pageElement : pageElements) {

                Vinyl vinyl = Vinyl.builder()
                        .release(pageElement.getElementsByClass("margin-top-clear margin-bot-5").text())
                        .artist(pageElement.getElementsByClass("text-ellipsis").select("a").text())
                        .price(pageElement.getElementsByClass("pull-left margin-top-5 showcase-release-price").text())
                        .vinylLink("http://vinyl.ua" + pageElement.getElementsByClass("img-showcase-release").select("a").attr("href"))
                        .imageLink(getImageLink(pageElement))
                        .build();

                log.info("Vinyl: {}", vinyl);
                vinyls.add(vinyl);
            }
        }
    }

    private String getImageLink(Element pageElement) {
        List<String> styleAttr = List.of(pageElement.getElementsByClass("img-showcase-release").attr("style").split("'"));
        return styleAttr.get(1);
    }
}

