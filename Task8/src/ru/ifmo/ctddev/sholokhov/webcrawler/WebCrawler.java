package ru.ifmo.ctddev.sholokhov.webcrawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WebCrawler implements Crawler {
    private final Downloader downloader;
    private final FixedThreadPool downloadPool;
    private final FixedThreadPool extractPool;
    private final Set<String> downloadedPages;
    private final Set<String> extractedPages;
    private final ConcurrentHashMap<String, IOException> exceptionPages;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloadPool = new FixedThreadPool(downloaders);
        this.extractPool = new FixedThreadPool(extractors);
        this.downloadedPages = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.extractedPages = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.exceptionPages = new ConcurrentHashMap<>();
    }

    @Override
    public Result download(String url, int depth) throws IOException {
       // System.out.println("depth = " + depth);
        if (depth == 1) {
            return new Result(Arrays.asList(url), new HashMap<>());
        } else {
            List<String> list = new ArrayList<>();
            Queue<Pair> queue = new ConcurrentLinkedQueue<>();
            queue.add(new Pair(url, depth));

            while (!queue.isEmpty()) {
                Pair task = queue.poll();
                System.out.println(queue.size());
                if (task.depth == 1) {
                    list.add(task.URL);
                }
                else if (!downloadedPages.contains(task.URL)){
                    downloadedPages.add(task.URL);
                    AtomicInteger v = new AtomicInteger(1);
                    downloadPool.execute( () -> {
                        try {
                            Document document = downloader.download(task.URL);
                            extractPool.execute(() -> {
                                try {
                                    list.add(task.URL);
                                    if (!extractedPages.contains(task.URL)) {
                                        extractedPages.add(task.URL);
                                        document.extractLinks().forEach(s -> queue.add(new Pair(s, task.depth - 1)));
                                    }

                                } catch (IOException e) {
                                    exceptionPages.put(task.URL, e);
                                } finally {
                                    v.set(0);
                                }
                            });

                        } catch (IOException e) {
                            exceptionPages.put(task.URL, e);
                            v.set(0);
                        }
                    });
                    while (!v.compareAndSet(0, 1));
                }
            }
            return new Result(list, exceptionPages);
        }
    }


    private class Pair {
        String URL;
        int depth;
        Pair(String URL, int depth) {
            this.URL = URL;
            this.depth = depth;
        }
    }


    @Override
    public void close() {
        downloadPool.shutdown();
        extractPool.shutdown();
    }
}
