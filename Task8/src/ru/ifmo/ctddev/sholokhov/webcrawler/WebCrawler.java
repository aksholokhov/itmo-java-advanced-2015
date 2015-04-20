package ru.ifmo.ctddev.sholokhov.webcrawler;

import info.kgeorgiy.java.advanced.crawler.Crawler;
import info.kgeorgiy.java.advanced.crawler.Document;
import info.kgeorgiy.java.advanced.crawler.Downloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebCrawler implements Crawler {
    Downloader downloader;
    int downloaders;
    int extractors;
    int perHost;

    FixedThreadPool downloadPool;
    FixedThreadPool extractPool;

    WebCrawler (Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloaders = downloaders;
        this.extractors = extractors;
        this.perHost = perHost;

        downloadPool = new FixedThreadPool(downloaders);
        extractPool = new FixedThreadPool(extractors);
    }


    @Override
    public List<String> download(String url, int depth) throws IOException {
        if (depth == 1) {
            return Arrays.asList(url);
        } else {
            FinishTrigger trigger = new FinishTrigger(1);
            Document doc = downloader.download(url);
            List<String> answer = new ArrayList<>();

            downloadPool.execute(new ThrowedRunnable() {
                @Override
                public void run() throws Throwable{
                    answer.add(url);
                    extractPool.execute(new Subtask(trigger, new ThrowedRunnable() {
                        @Override
                        public void run() throws Throwable {
                          // doc.extractLinks().stream().forEach(link -> answer.addAll(download(link, depth - 1)));
                            List<String> links = doc.extractLinks();
                            for(String link : links) {
                                answer.addAll(download(link, depth -1));
                            }
                        }
                    }));


                }
            });
            do {

            } while (trigger.)
        }


    }

    @Override
    public void close() {

    }
}
