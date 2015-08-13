package ru.ifmo.ctddev.sholokhov.helloudp;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * My dummy implementation of the HelloServer interface
 */

public class HelloUDPServer implements HelloServer {
    ExecutorService pool;

    /**
     * Starts the server binded to the {@code port} and charged with {@code threads}
     * @param port which the server will bind
     * @param threads number of threads the server operate with
     */
    @Override
    public void start(int port, int threads) {
        pool = Executors.newFixedThreadPool(threads);
        final DatagramSocket socket;

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
                        byte[] buf;
                        DatagramPacket p;
                        try {
                            buf = new byte[socket.getReceiveBufferSize()];
                            p = new DatagramPacket(buf, buf.length);
                            socket.receive(p);
                    //        System.out.println("fff " + p.getLength());
                        } catch (IOException e) {
                            System.out.println("Exception during receiving");
                            return;
                        }

                        byte[] response = ("Hello, " + new String(p.getData(), 0, p.getLength())).getBytes(Charset.forName("utf-8"));
                        //System.out.println("gggg " + new String(response, 0, response.length) + " " + response.length);
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
