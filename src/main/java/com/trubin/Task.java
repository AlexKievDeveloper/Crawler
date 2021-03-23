package com.trubin;

//https://www.baeldung.com/java-with-jsoup
//https://www.baeldung.com/java-download-file

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

public class Task {
    final static WebClient WEB_CLIENT = new WebClient();

    static {
        WEB_CLIENT.getOptions().setThrowExceptionOnScriptError(false);
        WEB_CLIENT.getOptions().setThrowExceptionOnFailingStatusCode(false);
    }

    static class ZNOInfo {
        String name;
        String year;
        String link;

        public ZNOInfo(String name, String year, String link) {
            this.name = name;
            this.year = year;
            this.link = link;
        }

        @Override
        public String toString() {
            return "ZNOInfo{" +
                    "name='" + name + '\'' +
                    ", year='" + year + '\'' +
                    ", link='" + link + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) throws Exception {
        // 1. parse ZNO years and exams types with links
        List<ZNOInfo> znoInfoList = getZNOInfo("https://zno.osvita.ua/mathematics/");

        for (ZNOInfo znoInfo : znoInfoList) {

            // 2. DownLoad images for ZNO
            downloadImages(znoInfo);
        }
    }


    private static List<ZNOInfo> getZNOInfo(String link) throws IOException {
        List<ZNOInfo> znoInfos = new ArrayList<>();

        // 1. get all li with class = 'test-item'
        final HtmlPage page = WEB_CLIENT.getPage(link);
        DomNodeList<DomNode> nodeList = page.querySelectorAll("li.test-item");

        for (DomNode domNode : nodeList) {
            // 2. from each li get a (href in a is going to be link to ZNO test)
            DomNode linkNode = domNode.getFirstChild();
            NamedNodeMap attributes = linkNode.getAttributes();
            String href = "https://zno.osvita.ua" + attributes.getNamedItem("href").getTextContent();

            // 3. from span['year'] get first 4 characters
            String year = domNode.querySelector("span.year").getTextContent().substring(0, 4);

            // 4. get ZNO name from a, exclude all tag informantion
            String textContent = linkNode.getTextContent();


            // 5. add to collection
            ZNOInfo znoInfo = new ZNOInfo(textContent, year, href);
            System.out.println(znoInfo);
            znoInfos.add(znoInfo);
        }

        return znoInfos;
    }


    private static void downloadImages(ZNOInfo znoInfo) throws IOException {
        // 1. Get path to folder
        String folderPrefix = new File(String.valueOf(znoInfo.year), znoInfo.name)
                .getAbsolutePath();
        System.out.println("folderPrefix = " + folderPrefix);

        // 2. get links to all images from znoInfo link

        HtmlPage page = WEB_CLIENT.getPage(znoInfo.link);

        DomNodeList<DomNode> imgLinks = page.querySelectorAll("div.question img");
        List<String> imageUrls = new ArrayList<>();
        for (DomNode imgLink : imgLinks) {
            Node src = imgLink.getAttributes().getNamedItem("src");
            imageUrls.add(src.getTextContent());
        }

        new File(folderPrefix).mkdirs();

        for (String imageUrl : imageUrls) {
            System.out.println("image Url = " + imageUrl);
            URL website = new URL("https://zno.osvita.ua/" + imageUrl);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            System.out.println("fileName = " + fileName);
            FileOutputStream fos = new FileOutputStream(new File(folderPrefix, fileName));
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
}