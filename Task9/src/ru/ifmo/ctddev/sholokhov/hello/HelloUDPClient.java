package ru.ifmo.ctddev.sholokhov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloUDPClient implements HelloClient {
    @Override
    public void start(String host, int p, String pref, int req, int threads) {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        final InetAddress addr;
        final int port = p;
        final int requests = req;
        final String prefix = pref;
        try {
            addr = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host, exiting...");
            pool.shutdown();
            return;
        }


        for (int i = 0; i < threads; i++) {
            pool.execute(new SocketThread(i) {
                @Override
                public void run() {
                    DatagramSocket socket;
                    try {
                        socket = new DatagramSocket();
                        socket.setSoTimeout(200);
                    } catch (SocketException e) {
                        System.out.println("Cannot establish the connection with the server, exititng...");
                      // e.printStackTrace();
                        return;
                    }
                    int j = 0;
                    while (j < requests) {
                        String request = prefix+num+"_"+j;
                        DatagramPacket packet = new DatagramPacket(request.getBytes(Charset.forName("utf-8")), request.length(), addr, port);
                        try {
                            socket.send(packet);
                        } catch (IOException e) {
                            System.out.println("IOException during sending package. Thread: " + num + ", iter: " + j +". Exiting...");
                            return;
                        }

                        try {
                            byte[] buf = new byte[socket.getReceiveBufferSize()];
                            DatagramPacket packet2 = new DatagramPacket(buf, buf.length);
                            socket.receive(packet2);
                            String response = new String(buf, 0, packet2.getLength());
                            if (response.equals("Hello, " + request)) {
                               // System.out.println(request);
                               // System.out.println(response);
                                j++;
                            } else {
                               // System.out.println( response + " != " + "Hello, " + request );
                            }
                        } catch (SocketTimeoutException e) {
                        //    System.out.println("Thread " + num + ", iter " + j + " STOException, resending");
                        } catch (IOException e) {
                        //    System.out.println("IOException during receiving package. Thread: " + num + ", iter: " + j);
                        }


                    }
                }
            });
        }
        pool.shutdown();
        while (!pool.isTerminated());
    }
}
