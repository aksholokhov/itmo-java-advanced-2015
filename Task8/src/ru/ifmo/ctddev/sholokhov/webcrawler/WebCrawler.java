package ru.ifmo.ctddev.sholokhov.webcrawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WebCrawler implements Crawler {
    private final Downloader downloader;
    private final ExecutorService downloadPool;
    private final ExecutorService extractPool;
    private final Set<String> downloadedPages;
    private final Set<String> extractedPages;
    public Map<String, List<Runnable>> perHostQueue;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloadPool = Executors.newFixedThreadPool(downloaders);
        this.extractPool = Executors.newFixedThreadPool(extractors);
        this.downloadedPages = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.extractedPages = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.perHostQueue = new TreeMap<>();
    }

    @Override
    public Result download(String url, int depth) throws IOException {
        if (depth == 1) {
            return new Result(Arrays.asList(url), new HashMap<>());
        } else {
            Set<String> links = new TreeSet<>();
            HashMap<String, IOException> errs = new HashMap<>();
            AtomicInteger v = new AtomicInteger(1);
            Runnable task1 = new Runnable() {
                @Override
                public void run() {
                    try {
                        Document document;
                        synchronized (downloader) {
                            document = downloader.download(url);
                        }
                        Subtask task =  new Subtask(v) {
                            @Override
                            public void run() {
                                synchronized (links) {
                                    links.add(url);
                                }
                                try {
                                    List<String> extractedLinks;
                                    synchronized (document) {
                                        extractedLinks = document.extractLinks();
                                    }
                                    for (String link : extractedLinks) {
                                        try {
                                            Result res = download(link, depth - 1);
                                            synchronized (links) {
                                                links.addAll(res.getDownloaded());
                                            }
                                            synchronized (errs) {
                                                errs.putAll(res.getErrors());
                                            }
                                        } catch (IOException e) {
                                            synchronized (errs) {
                                                errs.put(link, e);
                                            }
                                        } finally {
                                            v.set(0);
                                        }
                                    }

                                } catch (IOException e) {
                                    //      System.err.println("Error in extract links");
                                }

                            }
                        };
                        synchronized (extractPool){
                            if (!extractPool.isShutdown()) extractPool.submit(task);
                        }
                    } catch (IOException e) {
                        synchronized (errs) {
                            errs.put(url, e);
                        }
                        v.set(0);
                    }
                }

            };
            synchronized (downloadPool) {
                if (!downloadPool.isShutdown()) downloadPool.submit(task1);
            }
            while (!v.compareAndSet(0, 1)) ;
            return new Result( new ArrayList<>(links), errs);
        }
    }


    @Override
    public void close() {
        downloadPool.shutdownNow();
        extractPool.shutdownNow();
        while (!downloadPool.isShutdown());
        while (!extractPool.isShutdown());
    }
}
