package ru.ifmo.ctddev.sholokhov.webcrawler;

import info.kgeorgiy.java.advanced.crawler.CachingDownloader;
import info.kgeorgiy.java.advanced.crawler.Crawler;

import java.io.IOException;

/**
 * Created by Шолохов on 22.04.2015.
 */
public class Main {
    public static void main(String[] args) {
        try {
            //String url = args[0];
            String url = "http://rain.ifmo.ru/~komarov/y2013/";
            int downloads = 2;
            int extractors = 2;
            int perHost = 1;
            int depth = 3;

   /*         if (args.length >= 2) {
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
                for (String link : crawler.download(url, depth)) {
                    System.out.println("-> " + link);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.out.println("Usage: WebCrawler url [downloads [extractors [perHost]]]");
            e.printStackTrace();
        }
    }
}
