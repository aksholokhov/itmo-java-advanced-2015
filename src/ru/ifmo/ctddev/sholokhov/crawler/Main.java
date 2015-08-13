package ru.ifmo.ctddev.sholokhov.crawler;

import info.kgeorgiy.java.advanced.crawler.CachingDownloader;
import info.kgeorgiy.java.advanced.crawler.Result;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args == null || args.length < 1 || args.length > 4) {
            System.err.println("Incorrect intput format");
            return;
        }

        String url;
        int downloaders = 10, extractors = 10, perHost = 10;
        try {
            url = args[0];
            if (args.length > 1) {
                downloaders = Integer.parseInt(args[1]);
            }
            if (args.length > 2) {
                extractors = Integer.parseInt(args[2]);
            }
            if (args.length > 3) {
                perHost = Integer.parseInt(args[3]);
            }
        } catch (NullPointerException | NumberFormatException e) {
            System.err.println("Incorrect intput format");
            return;
        }

        try (WebCrawler crawler = new WebCrawler(new CachingDownloader(new File("./temp/")), downloaders, extractors, perHost)) {
            Result links = crawler.download(url, 2);
            for (String s : links.getDownloaded()) {
                System.out.println("=>" + s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
