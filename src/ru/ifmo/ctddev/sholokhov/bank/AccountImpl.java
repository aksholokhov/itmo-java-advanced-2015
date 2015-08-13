package ru.ifmo.ctddev.sholokhov.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Threadsafe implementation of {@link Account} interface. Has
 * long variable inside describing balance and final String that describes account id.
 */
public class AccountImpl extends UnicastRemoteObject implements Account {
    private AtomicLong balance;
    private final String accountId;

    public AccountImpl(String id) throws RemoteException {
        super();
        accountId = id;
        balance = new AtomicLong(0);
    }

    public long getBalance() {
        return balance.longValue();
    }

    public void setBalance(long newAmount) {
        balance.set(newAmount);
    }

    public long increaseBalance(long delta) {
        return balance.addAndGet(delta);
    }

    public long decreaseBalance(long delta) {
        return balance.updateAndGet(s -> s - delta);
    }

    @Override
    public String getId() throws RemoteException {
        return accountId;
    }
}
