package ru.ifmo.ctddev.sholokhov.helloudp;

public abstract class SocketThread  implements Runnable {
    int num;

    SocketThread(int num) {
        this.num = num;
    }
}
