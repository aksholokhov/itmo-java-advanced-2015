package ru.ifmo.ctddev.sholokhov.webcrawler;

import info.kgeorgiy.java.advanced.crawler.Crawler;
import info.kgeorgiy.java.advanced.crawler.Document;
import info.kgeorgiy.java.advanced.crawler.Downloader;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class WebCrawler implements Crawler {
    Downloader downloader;
    int downloaders;
    int extractors;
    int perHost;

    ExecutorService downloadPool;
    ExecutorService extractPool;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloaders = downloaders;
        this.extractors = extractors;
        this.perHost = perHost;

        downloadPool = Executors.newFixedThreadPool(downloaders);
        extractPool = Executors.newFixedThreadPool(extractors);
    }


    @Override
    public List<String> download(String url, int depth) throws IOException {
        if (depth == 1) {
            return Arrays.asList(url);
        } else {
            Set<String> links = new TreeSet<>();
            AtomicInteger v = new AtomicInteger(1);
            downloadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Document document = downloader.download(url);
                        extractPool.execute(new Subtask(v) {
                            @Override
                            public void run() {
                                links.add(url);
                                try {
                                    for (String link : document.extractLinks()) {
                                        links.addAll(download(link, depth - 1));
                                    }
                                } catch (IOException e) {
                         //           e.printStackTrace();
                                }
                                finally {
                                    v.set(0);
                                }

                            }
                        });
                    } catch (IOException e) {
                       // e.printStackTrace();
                        v.set(0);
                    }
                }

            });
            while (!v.compareAndSet(0, 1));

            return new ArrayList(links);
        }

    }

    @Override
    public void close() {
        downloadPool.shutdown();
        extractPool.shutdown();
    }
}
