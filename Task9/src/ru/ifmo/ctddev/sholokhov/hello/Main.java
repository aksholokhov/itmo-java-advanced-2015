package ru.ifmo.ctddev.sholokhov.hello;

public class Main {
    public static void main(String[] args) {
        HelloUDPClient client = new HelloUDPClient();
        HelloUDPServer server = new HelloUDPServer();

        server.start(15700, 8);
        client.start("localhost", 15700, "prefix", 3, 4);
    }
}
