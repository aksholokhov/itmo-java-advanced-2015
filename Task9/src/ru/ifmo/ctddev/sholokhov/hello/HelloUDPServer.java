package ru.ifmo.ctddev.sholokhov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloUDPServer implements HelloServer {
    ExecutorService pool;

    @Override
    public void start(int port, int threads) {
        pool = Executors.newFixedThreadPool(threads);
        final DatagramSocket socket;
        final String prefix = "prefix";

        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("Exception in socket creation");
            e.printStackTrace();
            return;
        }
        for (int i = 0; i < threads; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        byte[] buf = new byte[256];
                        DatagramPacket p = new DatagramPacket(buf, buf.length);
                        try {
                            socket.receive(p);
                        } catch (IOException e) {
                            System.out.println("Exception during receiving");
                            return;
                        }

                        byte[] response = ("Hello, " + new String(buf, 0, p.getLength())).getBytes(Charset.forName("utf-8"));
                       // System.out.println(">" + new String(response) + "<");
                        try {
                            socket.send(new DatagramPacket(response, response.length, p.getAddress(), p.getPort()));
                        } catch (IOException e) {
                            System.out.println("Exception during sending");
                        }

                    }
                }
            });
        }

    }

    @Override
    public void close() {
        pool.shutdown();
    }
}
