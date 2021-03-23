package com.orlovkiy2;

import com.orlovkiy2.entity.Vinyl;
import com.orlovkiy2.service.ShopService;
import com.orlovkiy2.service.impl.VinylUAShopService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) {
        ShopService shopService = new VinylUAShopService();
        List<Vinyl> vinyls = shopService.getVinyls();

        log.info("Всего виниловых пластинок: {}", vinyls.size());
        for (Vinyl vinyl : vinyls) {
            log.info("Vinyl: {}", vinyl);
        }
    }
}






















/*    static HashSet<String> links = new HashSet<>();
    static String s;
    static int innerCount;
    static int outerCount;*/

/*https://vinyl.com.ua/
        List<VinylPlate> dataOfProducts = new ArrayList<>();
        Document doc = Jsoup.connect("https://vinyl.com.ua/").get();

        Elements pageElements = doc.getElementsByClass("sr-gallery-item");
        for (Element pageElement : pageElements){
            String price = pageElement.getElementsByClass("pricik").text();
            String title = pageElement.getElementsByClass("sr-gallery-item-heading-title").text();
            String link = pageElement.getElementsByClass("sr-gallery-item-heading-title").select("a").attr("href");
            VinylPlate vinylPlate = new VinylPlate();
            vinylPlate.setPrice(price);
            vinylPlate.setArtist(title);
            vinylPlate.setVinylLink(link);
            dataOfProducts.add(vinylPlate);
        }
        for (int i = 0; i < dataOfProducts.size(); i++) {
            System.out.println();
            System.out.println(dataOfProducts.get(i).getArtist());
            System.out.println(dataOfProducts.get(i).getPrice());
            System.out.println(dataOfProducts.get(i).getVinylLink());
            System.out.println();
            System.out.println("-------------------------------");
        }
        System.out.println(dataOfProducts.size());

http://vinyl.ua/showcase/jazz
      List<VinylPlate> dataOfProducts = new ArrayList<>();
    Document doc = Jsoup.connect("http://vinyl.ua/showcase/jazz").get();


HashSet<String> links = getLinks("http://vinyl.ua");


        Elements innerLinks = doc.getElementsByTag("a");
        for (Element innerLink : innerLinks){
            links.add("http://vinyl.ua" + innerLink.attr("href"));
        }
System.out.println(links.size());*/

/*
        Elements pageElements = doc.getElementsByClass("vinyl-release showcase");
        for (Element pageElement : pageElements) {
            String release = pageElement.getElementsByClass("margin-top-clear margin-bot-5").text();
            String artist = pageElement.getElementsByClass("text-ellipsis").select("a").text();
            String price = pageElement.getElementsByClass("pull-left margin-top-5 showcase-release-price").text();
            String vinylLink = "http://vinyl.ua" + pageElement.getElementsByClass("img-showcase-release").select("a").attr("href");
            String[] styleAttr = pageElement.getElementsByClass("img-showcase-release").attr("style").split("'");
            String imageLink = styleAttr[1];

            VinylPlate vinylPlate = new VinylPlate();
            vinylPlate.setRelease(release);
            vinylPlate.setArtist(artist);
            vinylPlate.setPrice(price);
            vinylPlate.setVinylLink(vinylLink);
            vinylPlate.setImageLink(imageLink);
            dataOfProducts.add(vinylPlate);
        }
        for (int i = 0; i < dataOfProducts.size(); i++) {
            System.out.println();
            System.out.println(dataOfProducts.get(i).getRelease());
            System.out.println(dataOfProducts.get(i).getArtist());
            System.out.println(dataOfProducts.get(i).getPrice());
            System.out.println(dataOfProducts.get(i).getVinylLink());
            System.out.println(dataOfProducts.get(i).getImageLink());
            System.out.println();
            System.out.println("-------------------------------");
        }
        System.out.println(dataOfProducts.size());
    }

    static HashSet<String> getLinks(String url) throws IOException {

        Document doc = Jsoup.connect(url).get();
        Elements innerLinks = doc.getElementsByTag("a");
        for (Element innerLink : innerLinks) {
            String s;
            if (innerLink.attr("href").contains("www") || innerLink.attr("href").contains("http")) {
                s = innerLink.attr("href");
            } else {
                s = "http://vinyl.ua" + innerLink.attr("href");
            }

            if (s.contains("http://vinyl.ua/")) {
                links.add(s);
            }
        }
        Iterator<String> iteratorString = links.iterator();
        while (iteratorString.hasNext()) {
//            int i = 0;
            s=iteratorString.next();
            if (innerCount == outerCount){
              //  s = iteratorString.next();
                innerCount++;
                outerCount=0;
                System.out.println(s);
                System.out.println(links.size());
                if (!s.equals(url))
                getLinks(s);
            }
            outerCount++;

            //i++;
        }
        return links;
    }

    static class VinylPlate {
        private String release;
        private String artist;
        private String price;
        private String vinylLink;
        private String imageLink;

        public void setRelease(String release) {
            this.release = release;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public void setVinylLink(String vinylLink) {
            this.vinylLink = vinylLink;
        }

        public void setImageLink(String imageLink) {
            this.imageLink = imageLink;
        }

        public String getRelease() {
            return release;
        }

        public String getArtist() {
            return artist;
        }

        public String getPrice() {
            return price;
        }

        public String getVinylLink() {
            return vinylLink;
        }

        public String getImageLink() {
            return imageLink;
        }
    */


