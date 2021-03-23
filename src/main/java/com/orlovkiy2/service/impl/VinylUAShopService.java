package com.orlovkiy2.service.impl;


import com.orlovkiy2.entity.Vinyl;
import com.orlovkiy2.service.ShopService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
public class VinylUAShopService implements ShopService {
    //private volatile HashSet<String> allUniqueInnerLinks = new HashSet<>();
    //private Set<String> allUniqueInnerLinks = ConcurrentHashMap.newKeySet();
    private HashSet<String> allUniqueInnerLinks = new LinkedHashSet<>();
    private String innerLink;
    private int innerCount;
    private int outerCount;

    @Override
    public List<Vinyl> getVinyls() {
        /*HashSet<String> allUniqueInnerLinks = */
        readAllInnerLinks("http://vinyl.ua");
        log.info("Всего уникальных ссылок: {}", allUniqueInnerLinks.size());
        return readDataProductFromPage(allUniqueInnerLinks);
    }

    /**
     * Вычитывает все вложенные ссылки и возвращает уникальные
     */
    @SneakyThrows
    private /*HashSet<String>*/void readAllInnerLinks(String url) {
        /*HashSet<String> allUniqueInnerLinks = new HashSet<>();*/

        Document document = Jsoup.connect(url).get();

        //КУСОК КОДА КОТОРЫЙ ДОСТАЁТ ВСЕ ССЫЛКИ ПО НЫНЕШНЕЙ УРЛЕ И ДОБАВЛЯЕТ ВСЕ УНИКАЛЬНЫЕ В ХЭШСЭТ
        Elements innerLinks = document.getElementsByTag("a"); //ПОЛУЧАЕМ ВСЕ ССЫЛКИ СО СТРАНИЦЫ
        for (Element innerLink : innerLinks) {
            String link;
            if (innerLink.attr("href").contains("www") || innerLink.attr("href").contains("http")) {
                link = innerLink.attr("href");//ЕСЛИ ПОЛНАЯ ССЫЛКА ТО СРАЗУ ДОСТАЁМ ЕЁ
            } else {
                link = "http://vinyl.ua" + innerLink.attr("href");//ЕСЛИ НЕ ПОЛНАЯ ССЫЛКА ТО К ДОМЕНУ ДОБАВЛЯЕМ ВНУТРЕННЮЮ ССЫЛКУ
            }

            if ((link.contains("http://vinyl.ua/showcase/") || link.contains("http://vinyl.ua/release/")) && !link.contains(" http://vinyl.ua/showcase/ussr")) { //ПРОВЕРЯЕМ ЧТО ЭТО НЕ ССЫЛКА НА ВКОНТАКТЕ И НЕ НА ФЕЙСБУК

                if (link.contains(" }}")) { //Если ссылка содержит }} то обрезаем её
                    link = link.substring(0, link.length() - 3);
                }

                //ЕСЛИ ССЫЛКА УНИКАЛЬНА ДОБАВЛЯЕМ ЕЁ В СПИСОК УНИКАЛЬНЫХ ССЫЛОК
                if (allUniqueInnerLinks.add(link)) {
                    log.info("Уникальная ссылка добавлена в список уникальных ссылок: {}", link);//ДОБАВЛЯЮТСЯ НЕ УНИКАЛЬНЫЕ ССЫЛКИ 1) {} 2) хз ПОЧЕМУ, РАЗНЫЕ СТРИНГИ
                }
            }
        }

        log.info("Ссылок в хэш сэте: {}", allUniqueInnerLinks.size());

        //ЭТОТ КУСОК КОДА БЕЖИТ ПО СПИСКУ ВСЕХ УНИКАЛЬНЫХ ССЫЛОК И ЗАПУСКАЕТ ПОИСК ВСЕХ ССЫЛОК
        //НА ТЕХ ССЫЛКАХ НА КОТОРЫХ ОН ЕЩЁ НЕ ЗАПУСКАЛСЯ
        for (String allUniqueInnerLink : allUniqueInnerLinks) {
            innerLink = allUniqueInnerLink;
            if (innerCount == outerCount) {
                innerCount++;
                outerCount = 0;
//ПРОВЕРЯЕМ ЧТО ЕСЛИ ОДНА ИЗ УНИКАЛЬНЫХ ССЫЛОК НЕ СООТВЕТСТВУЕТ ИЗНАЧАЛЬНОЙ ССЫЛКЕ ТО МЫ ЗАНОВО ВЫЗЫВАЕМ МЕТОД ПО ВЫЧИТКЕ ВЛОЖЕННЫХ ССЫЛОК НА УНИКАЛЬНОЙ ССЫЛКЕ
                if (!innerLink.equals(url)) {//ЕСЛИ ЭТА ССЫЛКА НЕ РАВНА УРЛЕ КОТОРАЯ ПЕРЕД ЭТИМ ПРИШЛА (ЕСЛИ Я НА НЕЁ ЕЩЁ НЕ ЗАХОДИЛ)
                    readAllInnerLinks(innerLink);
                    break;
                }
            }
            outerCount++;
        }
    }

    /**
     * Вычитывает данные по ссылкам
     */
    @SneakyThrows
    private List<Vinyl> readDataProductFromPage(HashSet<String> allUniqueInnerLinks) {
        List<Vinyl> vinyls = new ArrayList<>();

        for (String uniqueInnerLink : allUniqueInnerLinks) {
            Document doc = Jsoup.connect(uniqueInnerLink).get();
            Elements pageElements = doc.getElementsByClass("vinyl-release showcase");
            for (Element pageElement : pageElements) {

                Vinyl vinyl = Vinyl.builder()
                        .release(pageElement.getElementsByClass("margin-top-clear margin-bot-5").text())
                        .artist(pageElement.getElementsByClass("margin-top-clear margin-bot-5").text())
                        .price(pageElement.getElementsByClass("pull-left margin-top-5 showcase-release-price").text())
                        .vinylLink("http://vinyl.ua" + pageElement.getElementsByClass("img-showcase-release").select("a").attr("href"))
                        .vinylLink(getImageLink(pageElement))
                        .build();

                vinyls.add(vinyl);
            }
        }

        return vinyls;
    }

    private String getImageLink(Element pageElement) {
        List<String> styleAttr = List.of(pageElement.getElementsByClass("img-showcase-release").attr("style").split("'"));
        return styleAttr.get(1);
    }

}









/**
 * Метод в котором парсим странички на предмет внутренних ссылок. Передаю в метод ссылку парсим страницу по ссылке и
 * достаём со страницы все ссылки которые есть отбрасываю все неподходящие ссылки и отставляю только те которые подходят
 * потом беру следующую ссылку которая уже в хэш сэте, который вынесен отдельной один хэш сэт
 * <p>
 * Циферка выводит количество уникальных ссылок и ссылки которые уже в хэш сэте
 * <p>
 * гетЛинк получает урлу по которой мы хотим парсить страницу чтобы вытянуть все внутренние линки
 */


//ЭТОТ КУСОК КОДА БЕЖИТ ПО СПИСКУ ВСЕХ УНИКАЛЬНЫХ ССЫЛОК И ЗАПУСКАЕТ ПОИСК ВСЕХ ССЫЛОК
//НА ТЕХ ССЫЛКАХ НА КОТОРЫХ ОН ЕЩЁ НЕ ЗАПУСКАЛСЯ
/*        log.info("Ссылок в хэш сэте: {}", allUniqueInnerLinks.size());
        for (String uniqueInnerLink : allUniqueInnerLinks) { //БЕЖИМ ПО СПИСКУ УНИКАЛЬНЫХ ССЫЛОК
            innerLink = uniqueInnerLink;
            if (innerCount == outerCount) { //ПЕРВЫЙ РАЗ ЗАЙДЁМ ПОТОМУ ЧТО ВСЁ ПО НУЛЯМ
                innerCount++; //СЧЁТЧИК КОЛИЧЕСТВА ПРОЧИТАННЫХ ССЫЛОК
                outerCount = 0;//КОЛИЧЕСТВО ССЫЛОК ПО КОТОРЫМ Я ПРОБЕГАЮСЬ

//ПРОВЕРЯЕМ ЧТО ЕСЛИ ОДНА ИЗ УНИКАЛЬНЫХ ССЫЛОК СООТВЕТСТВУЕТ ИЗНАЧАЛЬНОЙ ССЫЛКЕ ТО МЫ ЗАНОВО ВЫЗЫВАЕМ МЕТОД ПО ВЫЧИТКЕ ВЛОЖЕННЫХ ССЫЛОК НА УНИКАЛЬНОЙ ССЫЛКЕ
                if (!innerLink.equals(url)) { //ЕСЛИ ЭТА ССЫЛКА НЕ РАВНА УРЛЕ КОТОРАЯ ПЕРЕД ЭТИМ ПРИШЛА (ЕСЛИ Я НА НЕЁ ЕЩЁ НЕ ЗАХОДИЛ)
                    readAllInnerLinks(uniqueInnerLink);
                }
            }
            outerCount++;
        }*/


/*        for (int i = 0; i < allUniqueInnerLinks.size(); i++) {
            innerLink = allUniqueInnerLinks.;
            if (innerCount == outerCount) {
                innerCount++;
                outerCount = 0;

                if (!innerLink.equals(url)) {
                    readAllInnerLinks(innerLink);
                    break;
                }
            }
            outerCount++;
        }*/


/*return allUniqueInnerLinks;*/

//private String innerLink;

/*

                  System.out.println(innerLink);
                System.out.println(allUniqueInnerLinks.size());



  Iterator<String> iteratorLinksHashSet = allUniqueInnerLinks.iterator();
        while (iteratorLinksHashSet.hasNext()) {
            innerLink = iteratorLinksHashSet.next();
            if (innerCount == outerCount) {
                innerCount++;
                outerCount = 0;
                System.out.println(innerLink);
                System.out.println(allUniqueInnerLinks.size());
                if (!innerLink.equals(url)) {
                    readAllInnerLinks(innerLink);
                }
            }
            outerCount++;

            //!!!! temporarily
            if (allUniqueInnerLinks.size() > 10) {
                break;
            }
        }*/


//private List<Vinyl> vinyls;


/*        Iterator<String> iterator = allUniqueInnerLinks.iterator();
        while (iterator.hasNext()) {
            String url = iterator.next();*/


/*
*
        for (int i = 0; i < vinyls.size(); i++) {
            log.info("Data of products: {} +\n", vinyls);
        }
        log.info("Data size: {}", vinyls
                .size());*/

//private HashSet<String> allUniqueInnerLinks = new HashSet<>();

//private String url = "http://vinyl.ua";


/*            System.out.println();
            System.out.println(dataOfProducts.get(i).getRelease());
            System.out.println(dataOfProducts.get(i).getArtist());
            System.out.println(dataOfProducts.get(i).getPrice());
            System.out.println(dataOfProducts.get(i).getVinylLink());
            System.out.println(dataOfProducts.get(i).getImageLink());
            System.out.println();
            System.out.println("-------------------------------");*/

//System.out.println(dataOfProducts.size());


/*            String release = pageElement.getElementsByClass("margin-top-clear margin-bot-5").text();
            String artist = pageElement.getElementsByClass("text-ellipsis").select("a").text();
            String price = pageElement.getElementsByClass("pull-left margin-top-5 showcase-release-price").text();
            String vinylLink = "http://vinyl.ua" + pageElement.getElementsByClass("img-showcase-release").select("a").attr("href");


            Vinyl vinyl = new Vinyl();
            vinyl.setRelease(release);
            vinyl.setArtist(artist);
            vinyl.setPrice(price);
            vinyl.setVinylLink(vinylLink);
            vinyl.setImageLink(imageLink);*/