package ru.ifmo.ctddev.sholokhov.webcrawler;

import info.kgeorgiy.java.advanced.crawler.CachingDownloader;
import info.kgeorgiy.java.advanced.crawler.Crawler;
import info.kgeorgiy.java.advanced.crawler.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            String url = "http://en.ifmo.ru/en/page/50/Partnership.htm";
            int downloads = 10;
            int extractors = 10;
            int perHost = 1;
            int depth = 1;

/*            if (args.length >= 2) {
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
*/
            try (Crawler crawler = new WebCrawler(new CachingDownloader(), downloads, extractors, perHost)) {
                System.out.println("Downloaded:");
                Result r = crawler.download(url, depth);
                for (String link : r.getDownloaded()) {
                    System.out.println("-> " + link);
                }

                if (!r.getErrors().isEmpty()) {
                    System.out.println("ERRORS:");
                    for (Map.Entry e: r.getErrors().entrySet()) {
                        System.out.println(e.getKey() + "\n     " + e.getValue());
                    }
                }
                crawler.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.out.println("Usage: WebCrawler url [downloads [extractors [perHost]]]");
        }
    }
}
