package com.orlovskyi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ImageDownloader {

    private static final String URL = "https://zno.osvita.ua";
    private int folder = 0;

    public void downloadImages() throws IOException {
        Map<String, String> pageParameters = new HashMap<>();
        pageParameters.put("class", "test-item");
        pageParameters.put("tag", "a");
        pageParameters.put("attribute", "href");
        List<String> topicReferences = getReferences(URL+"/mathematics/", pageParameters);

        pageParameters.put("class", "question");
        pageParameters.put("tag", "img");
        pageParameters.put("attribute", "src");
        for (String topicReference : topicReferences){
            List<String> imagesPath = getReferences(topicReference, pageParameters);
            getImages(imagesPath);
        }
    }

    private List<String> getReferences(String url, Map<String, String> pageParameters) throws IOException {
        Document doc = Jsoup.connect(url).get();
        List<String> references = new ArrayList<>();
        Elements pageElements = doc.getElementsByClass(pageParameters.get("class")).select(pageParameters.get("tag"));
        for (Element pageElement : pageElements){
            references.add(URL+pageElement.attr(pageParameters.get("attribute")));
        }
        return references;
    }

    private void getImages(List<String> imagesPath){
        int i = 0;
        folder++;
        File dir = new File(String.valueOf(folder));
        dir.mkdir();
        for (String imagePath : imagesPath){
            i++;
            try (BufferedInputStream in = new BufferedInputStream(new URL(imagePath).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(folder+"/"+i+".png")) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
