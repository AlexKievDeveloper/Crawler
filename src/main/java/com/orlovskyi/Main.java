package com.orlovskyi;

import com.orlovskyi.ImageDownloader;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        ImageDownloader imageDownloader = new ImageDownloader();
        imageDownloader.downloadImages();
    }
}
