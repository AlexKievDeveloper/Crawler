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
    private int i = 0;

    public void downloadImages() throws IOException {
        Map<String, String> topicReferences = getTopicReferences(URL + "/mathematics/");
        for (Map.Entry<String, String> entry : topicReferences.entrySet()) {
            List<String> imagesPath = getReferences(entry.getKey());
            getImages(imagesPath, entry.getValue());
        }
    }

    private List<String> getReferences(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        List<String> references = new ArrayList<>();
        Elements pageElements = doc.getElementsByClass("question").select("img");
        for (Element pageElement : pageElements) {
            references.add(URL + pageElement.attr("src"));
        }
        return references;
    }

    private Map<String, String> getTopicReferences(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Map<String, String> yearForReference = new LinkedHashMap<>();
        Elements pageElements = doc.getElementsByClass("test-item").select("a");
        for (Element pageElement : pageElements) {
            yearForReference.put(URL + pageElement.attr("href"), pageElement.childNode(1).childNode(0).toString());
        }
        return yearForReference;
    }

    private void getImages(List<String> imagesPath, String folderName) {
        File dir = new File(folderName);
        if (!dir.exists()) {
            i=0;
            dir.mkdir();
        }
        for (String imagePath : imagesPath) {
            i++;
            try (BufferedInputStream in = new BufferedInputStream(new URL(imagePath).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(folderName + "/" + i + ".png")) {
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