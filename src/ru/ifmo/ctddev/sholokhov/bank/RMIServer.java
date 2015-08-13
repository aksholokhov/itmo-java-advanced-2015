package ru.ifmo.ctddev.sholokhov.bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * This is my implementation
 */

public class RMIServer {
    public static void main(String[] args) {
        try {
            Bank bank = new BankImpl();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("//localhost/bank", bank);
            System.out.println("Successfully started bank server");
        } catch (RemoteException | MalformedURLException e) {
            System.err.println("Couldn't start the bank server");
            e.printStackTrace();
        }
    }
}
