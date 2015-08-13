package ru.ifmo.ctddev.sholokhov.helloudp;

public class Main {
    public static void main(String[] args) {
        HelloUDPClient client = new HelloUDPClient();
        HelloUDPServer server = new HelloUDPServer();

        server.start(15750, 8);
        client.start("localhost", 15750, "ыкевчапавчп", 3, 4);
    }
}
