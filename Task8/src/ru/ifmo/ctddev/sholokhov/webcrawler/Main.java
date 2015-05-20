package ru.ifmo.ctddev.sholokhov.webcrawler;

import info.kgeorgiy.java.advanced.crawler.CachingDownloader;
import info.kgeorgiy.java.advanced.crawler.Crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            String url = args[0];
            int downloads = 1;
            int extractors = 1;
            int perHost = 1;
            int depth = 3;

            if (args.length >= 2) {
                downloads = Integer.parseInt(args[1]);
            }
            if (args.length >= 3) {
                extractors = Integer.parseInt(args[2]);
            }
            if (args.length == 4) {
                perHost = Integer.parseInt(args[3]);
            }
            if (args.length < 2 || args.length > 4) {
                throw new NullPointerException();
            }

            try (Crawler crawler = new WebCrawler(new CachingDownloader(), downloads, extractors, perHost)) {
                List<String> urls = crawler.download(url, depth);
                List<String> uniqueUrls = new ArrayList<>(new HashSet<>(urls));
                System.out.printf("%d %d\n", urls.size(), uniqueUrls.size());
                for (String link : urls) {
                    System.out.println("-> " + link);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.out.println("Usage: WebCrawler url [downloads [extractors [perHost]]]");
        }
    }
}
