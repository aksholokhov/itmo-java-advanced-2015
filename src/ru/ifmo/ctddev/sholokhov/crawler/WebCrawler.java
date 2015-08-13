package ru.ifmo.ctddev.sholokhov.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;


/**
 * This class is concurent implementation of {@link info.kgeorgiy.java.advanced.crawler.Crawler} interface.
 * It can download your site recursively for needed depth.
 *
 * @see info.kgeorgiy.java.advanced.crawler.Crawler
 */

public class WebCrawler implements Crawler {
    private final Downloader downloader;
    private final ExecutorService downloaders;
    private final ExecutorService extractors;
    private final int perHost, downloadsLimit, extractsLimit;

    /**
     * Main constructor.
     *
     * @param downloader  your class for downloading sites.
     * @param downloaders max downloaders threads count
     * @param extractors  max extractors threads count
     * @param perHost     max downloads from one host //it's not admitted
     */
    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloadsLimit = downloaders;
        this.extractsLimit = extractors;
        this.perHost = perHost;
        this.downloaders = Executors.newFixedThreadPool(downloaders);
        this.extractors = Executors.newFixedThreadPool(extractors);
    }

    /**
     * Downlnoads all sites by their links recursively.
     *
     * @param url   site to download
     * @param depth depth for download
     * @return Result
     */
    public Result download(String url, int depth) {

        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        ConcurrentHashMap<String, Boolean> visitedLinks = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, IOException> errorLinks = new ConcurrentHashMap<>();

        queue.add(url);
        visitedLinks.put(url, true);

        for (int d = 0; d < depth; d++) {
            final int innerDepth = d;
            ConcurrentLinkedQueue<String> innerQueue = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<Future> awaitingForResult = new ConcurrentLinkedQueue<>();

            ConcurrentLinkedQueue<String> innerQueue2 = new ConcurrentLinkedQueue<>();
            for (String link : queue) {
                synchronized (innerQueue) {
                    if (!innerQueue.contains(link)) innerQueue.add(link);
                }
                awaitingForResult.add(downloaders.submit(() -> {
                    String currentLink = "";
                    try {
                        while (true) {
                            currentLink = "";
                            synchronized (innerQueue) {
                                if (!innerQueue.isEmpty()) {
                                    currentLink = innerQueue.poll();
                                }
                            }
                            if (!currentLink.equals("")) {
                                final String curCopy = currentLink;
                                Document document = downloader.download(currentLink);
                                if (innerDepth + 1 == depth) {
                                    continue;
                                }
                                awaitingForResult.add(extractors.submit(() -> {
                                    try {
                                        List<String> links = document.extractLinks();
                                        for (String newLink : links) {
                                            synchronized (visitedLinks) {
                                                visitedLinks.putIfAbsent(newLink, false);
                                                if (!visitedLinks.get(newLink)) {
                                                    visitedLinks.put(newLink, true);
                                                    innerQueue2.add(newLink);
                                                }
                                            }
                                        }
                                    } catch (IOException e) {
                                        errorLinks.putIfAbsent(curCopy, e);
                                    }
                                }));
                            } else {
                                return;
                            }
                        }
                    } catch (IOException e) {
                        errorLinks.putIfAbsent(currentLink, e);
                    }
                }));
            }
            while (!awaitingForResult.isEmpty()) {
                Future cur = awaitingForResult.poll();
                try {
                    cur.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            queue.clear();
            innerQueue2.stream().distinct().forEach(queue::add);
        }

        errorLinks.keySet().forEach(visitedLinks::remove);

        return new Result(new ArrayList<>(visitedLinks.keySet()), new HashMap<>(errorLinks));
    }


    /**
     * Close object. It can't be used anymore after closing!
     */
    @Override
    public void close() {
        close(downloaders);
        close(extractors);
    }

    /**
     * The orthodox way to shutdown ExecutorService from oracle.javadoc examples.
     *
     * @param service
     */
    private void close(ExecutorService service) {
        service.shutdown();
        if (!service.isShutdown()) {
            try {
                service.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                service.shutdownNow();
            }
        }
    }
}