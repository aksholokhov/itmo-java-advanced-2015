package ru.ifmo.ctddev.sholokhov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for operating with person.
 */
public interface Person extends Remote {
    /**
     * Returns person's name
     * @return name of the person
     * @throws RemoteException
     */
    String getName() throws RemoteException;

    /**
     * Returns second person's name (surname)
     * @return surname of the person
     * @throws RemoteException
     */
    String getSurname() throws RemoteException;

    /**
     * Returns person's identity (passport)
     * @return identity of the person
     * @throws RemoteException
     */
    String getId() throws RemoteException;

    /**
     * Returns the type of person object -- local or remote
     * @return person's type
     * @throws RemoteException
     */
    PersonType getType() throws RemoteException;
}
