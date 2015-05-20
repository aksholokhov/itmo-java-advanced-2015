package ru.ifmo.ctddev.sholokhov.webcrawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class WebCrawler implements Crawler {
    private Downloader downloader;
    private FixedThreadPool downloadPool;
    private FixedThreadPool extractPool;
    private Set<String> downloadedPages;
    private Set<String> extractedPages;
    public Map<String, List<Runnable>> perHostQueue;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloadPool = new FixedThreadPool(downloaders, perHost);
        this.extractPool = new FixedThreadPool(extractors, Integer.MAX_VALUE);
        this.downloadedPages = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.extractedPages = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.perHostQueue = new TreeMap<>();
    }

    @Override
    public Result download(String url, int depth) throws IOException {
        if (depth == 1) {
            return new Result(Arrays.asList(url), new HashMap<>());
        } else {
            List<String> links = new ArrayList<>();
            IOException[] maybeE = new IOException[1];
            maybeE[0] = null;

            Queue<URLTask> queue = new ConcurrentLinkedQueue<>();
            queue.add(new URLTask(url, depth));

            while (!queue.isEmpty()) {
                URLTask task = queue.poll();
                System.out.println(queue.size());

                if (task.depth == 1) {
                    links.add(task.url);
                } else if (!downloadedPages.contains(task.url)) {
                    downloadedPages.add(task.url);
                    AtomicInteger v = new AtomicInteger(1);
                    String host = URLUtils.getHost(task.url);

                    downloadPool.execute(host, () -> {
                        try {
                            Document document = downloader.download(task.url);

                            extractPool.execute(host, () -> {
                                try {
                                    links.add(task.url);

                                    if (!extractedPages.contains(task.url)) {
                                        extractedPages.add(task.url);
                                        for (String link : document.extractLinks()) {
                                            queue.add(new URLTask(link, task.depth - 1));
                                        }
                                    }
                                } catch (IOException e) {
                                    maybeE[0] = e;
                                } finally {
                                    v.set(0);
                                }
                            });
                        } catch (IOException e) {
                            maybeE[0] = e;
                            v.set(0);
                        }
                    });

                    while (!v.compareAndSet(0, 1));

                    if (maybeE[0] != null) {
                        throw maybeE[0];
                    }
                }
            }

            return links;
        }
    }

    @Override
    public void close() {
        downloadPool.shutdown();
        extractPool.shutdown();
    }

    private final class URLTask {
        public final String url;
        public final int depth;

        public URLTask(String url, int depth) {
            this.url = url;
            this.depth = depth;
        }
    }
}
